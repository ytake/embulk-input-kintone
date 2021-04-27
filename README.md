# Kintone input plugin for Embulk

For Embulk >= 0.10.19

*wip filter plugin

## Overview

* **Plugin type**: input
* **Resume supported**: yes
* **Cleanup supported**: yes
* **Guess supported**: no

## Configuration

### 

```yaml
in:
  type: kintone
  domain: example.cybozu.com
  username: username
  password: password
  app_id: 1111
  fields:
    - {name: $id, type: long}
    - {name: $revision, type: long}
    - {name: foo, type: string}
```

## Usage

### install embulk

```bash
curl --create-dirs -o /usr/local/bin/embulk -L "https://github.com/embulk/embulk/releases/download/v0.10.19/embulk-0.10.19.jar" \\
 && chmod +x /usr/local/bin/embulk
```

### build

```bash
$ ./gradle classpath
```

### embulk run

```bash
$ embulk run -L /path/to/embulk-input-kintone/ your_config.yml
```
