# LogAnalyzer

A small, dependency-free Java CLI tool that scans a log file, classifies each line as `INFO`, `WARNING`, `ERROR`, or unclassified, and writes a durable, disk-synced summary report.

## Features

- **Line-by-line classification** of log entries into `INFO`, `WARNING`, `ERROR`, and unclassified buckets (case-insensitive substring matching).
- **Durable error logging** — each error line is written to the report and immediately flushed and `fsync`'d to disk, so critical errors survive a crash even if the process dies before finishing.
- **Permission pre-flight checks** — verifies the input file is readable and the output location (or its nearest existing parent directory) is writable before doing any work.
- **Automatic output directory creation** — creates the output file's parent directory if it doesn't already exist.
- **Zero dependencies** — pure JDK standard library (`java.io`, `java.nio.charset`), no build tool required.

## Requirements

- A JDK capable of compiling plain `java.io`/`java.nio` code. The IntelliJ project is configured for JDK 25 (`.idea/misc.xml`), but the code itself uses no JDK-25-specific APIs, so most modern JDKs (11+) should work.

## Build & Run

There's no build tool (no Maven/Gradle) — compile and run directly with `javac`/`java`:

```bash
javac -d out src/*.java
java -cp out LogAnalyzer [input_log_file] [output_report_file]
```

If no arguments are given, it falls back to defaults:

```bash
java -cp out LogAnalyzer
# equivalent to:
java -cp out LogAnalyzer logs/system.log reports/summary_report.txt
```

Show usage:

```bash
java -cp out LogAnalyzer --help
```

## Usage / CLI reference

| Argument             | Required | Default                     | Description                          |
|----------------------|----------|------------------------------|---------------------------------------|
| `input_log_file`     | No*      | `logs/system.log`            | Path to the log file to analyze       |
| `output_report_file` | No*      | `reports/summary_report.txt` | Path to write the summary report to   |
| `-h`, `--help`       | —        | —                             | Print usage and exit                  |

\* Both positional arguments must be supplied together, or omitted together — if exactly one is given, the tool falls back to the defaults for both.

**Exit codes:**

- `0` — analysis completed successfully
- `1` — input file missing/not a file, output directory couldn't be created, a permission check failed, or an I/O error occurred during processing

## How it works

| Class | Responsibility |
|---|---|
| `LogAnalyzer` | Entry point. Parses CLI args, validates the input/output paths, creates the output directory if needed, and orchestrates the parse → summarize flow. |
| `PermissionChecker` | Static pre-flight checks: confirms the input file is readable, and that the output file (or its nearest existing ancestor directory) is writable. |
| `LogParser` | Reads the input file line by line, uppercases and classifies each line by substring match (`INFO:`, `WARNING:`, `ERROR:`), and returns an `AnalysisResult` with per-category totals. |
| `ReportWriter` | Implements `AutoCloseable`. Writes each error line to the report immediately, flushing and `fsync`-ing it to disk for durability, then writes the final summary block. Closed automatically via try-with-resources. |

## Sample output

Given an input log like `logs/server.log`:

```
[2026-07-17 00:04:12] ERROR: Database connection failed during query execution: SELECT * FROM users WHERE id = ?. (Code: 503)
[2026-07-17 00:05:22] WARNING: Slow query detected (took 2500ms): UPDATE sessions SET expires_at = ?.
[2026-07-17 00:06:05] INFO: Cache cleared for user session store.
DEBUG: Verbose logs enabled on logger 'org.hibernate'.
```

The generated `reports/summary_report.txt` contains one `[CRITICAL ERROR] ->` line per error (written and synced as they're found), followed by the summary block:

```
[CRITICAL ERROR] -> [2026-07-17 00:04:12] ERROR: DATABASE CONNECTION FAILED DURING QUERY EXECUTION: SELECT * FROM USERS WHERE ID = ?. (CODE: 503)
...
===============================
        LOG ANALYSIS SUMMARY
===============================
Total Log Lines Processed: 19
INFO Messages: 8
WARNING Messages: 4
ERROR Messages: 5
Unclassified Lines: 2
===============================
```

## Project structure

```
.
├── src/
│   ├── LogAnalyzer.java       # entry point / orchestration
│   ├── LogParser.java         # line classification + AnalysisResult
│   ├── PermissionChecker.java # read/write permission pre-checks
│   └── ReportWriter.java      # durable report writing (AutoCloseable)
├── logs/
│   └── server.log             # sample input log
└── reports/
    └── summary_report.txt     # generated output report
```

## Known limitations

- Classification is plain, case-insensitive substring matching (`line.contains("ERROR:")`, etc.), not structured/regex parsing — lines that don't follow the `LEVEL:` convention (e.g. `DEBUG:` or free-form system messages) fall into the unclassified bucket.
- No automated test suite yet.
