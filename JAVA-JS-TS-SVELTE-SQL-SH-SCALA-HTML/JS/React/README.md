# Test Samples for Universal Vulnerability Scanner

This directory contains test cases for validating the scanner's detection capabilities.

## Test Case Structure

Each subdirectory represents a different test scenario:

### 1. `rsc_vulnerable_exact/`
**Purpose**: Test detection of exact vulnerable versions

**Contains**:
- `react-server-dom-webpack@19.0.0` (VULNERABLE)
- `react-server-dom-parcel@19.1.0` (VULNERABLE)
- `react-server-dom-turbopack@19.1.1` (VULNERABLE)
- `next@15.0.3` (VULNERABLE - range)

**Expected**: Scanner should flag all RSC packages as CRITICAL

---

### 2. `rsc_vulnerable_range/`
**Purpose**: Test detection of versions within vulnerable ranges

**Contains**:
- `react@19.1.0` (VULNERABLE - in range)
- `react-dom@19.1.0` (VULNERABLE - in range)
- Version specifiers: `^19.0.0`, `~19.1.0`

**Expected**: Scanner should detect range-based vulnerabilities

---

### 3. `rsc_safe_versions/`
**Purpose**: Test that safe versions are not flagged as vulnerable

**Contains**:
- `react@19.0.1` (SAFE - patched)
- `react-server-dom-webpack@19.0.1` (SAFE - patched)
- `react-server-dom-parcel@19.1.2` (SAFE - patched)
- `next@16.0.7` (SAFE - patched)

**Expected**: Scanner should mark these as SAFE or INFO

---

### 4. `rsc_mixed/`
**Purpose**: Test scanner with mix of vulnerable, safe, and unrelated packages

**Contains**:
- Mix of VULNERABLE, SAFE, and CLEAN packages
- Non-RSC packages: `express`, `lodash` (should be CLEAN)

**Expected**: Accurate classification of each package

---

### 5. `rsc_with_lockfile/`
**Purpose**: Test lockfile parsing with resolved versions

**Contains**:
- `package.json` with version ranges
- `package-lock.json` with exact resolved versions

**Expected**: Scanner should detect vulnerabilities from lockfile

---

## Running Tests

```bash
# Scan all test samples
python -m universal_vulnerability_scanner.main scan test_samples/

# Scan specific test case
python -m universal_vulnerability_scanner.main scan test_samples/rsc_vulnerable_exact/

# With JSON output
python -m universal_vulnerability_scanner.main scan test_samples/ --output test_results.json

# With Phoenix upload (if configured)
python -m universal_vulnerability_scanner.main scan test_samples/ --upload-phoenix
```

## Expected Results Summary

| Test Case | Vulnerable | Safe | Clean | Review |
|-----------|------------|------|-------|--------|
| rsc_vulnerable_exact | 4 | 0 | 2 | 0 |
| rsc_vulnerable_range | 4 | 0 | 0 | 0 |
| rsc_safe_versions | 0 | 6 | 0 | 0 |
| rsc_mixed | 2 | 1 | 4 | 0 |
| rsc_with_lockfile | 2 | 0 | 1 | 0 |

## Vulnerable Versions Reference

### React Server Components
- **react-server-dom-webpack**: 19.0.0, 19.1.0, 19.1.1, 19.2.0
- **react-server-dom-parcel**: 19.0.0, 19.1.0, 19.1.1, 19.2.0
- **react-server-dom-turbopack**: 19.0.0, 19.1.0, 19.1.1, 19.2.0

### React Core
- **react**: Ranges [19.0.0, 19.0.1), [19.1.0, 19.1.2), [19.2.0, 19.2.1)
- **react-dom**: Same ranges as react

### Next.js
- **next**: [15.0.0, 15.0.5), [16.0.0, 16.0.7)

## Adding New Test Cases

1. Create a new directory under `test_samples/`
2. Add `package.json` (and optionally lockfiles)
3. Document expected results
4. Run scanner to validate

