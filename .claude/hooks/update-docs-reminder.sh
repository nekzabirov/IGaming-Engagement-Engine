#!/bin/bash
# Stop hook: Remind Claude to update documentation when source files change
# without corresponding documentation updates.
#
# Logic:
#   1. Get all uncommitted changes (vs HEAD)
#   2. If source files changed but no doc files changed -> block with reminder
#   3. Determine WHICH doc files need updating based on change categories

CHANGED_FILES=$(git diff --name-only HEAD 2>/dev/null || true)

if [ -z "$CHANGED_FILES" ]; then
  exit 0
fi

# Separate source and doc files
SRC_FILES=$(echo "$CHANGED_FILES" | grep -E "^src/" || true)
DOC_FILES=$(echo "$CHANGED_FILES" | grep -E "(^CLAUDE\.md$|^\.claude/rules/)" || true)

# No source changes -> nothing to do
if [ -z "$SRC_FILES" ]; then
  exit 0
fi

# Doc files also changed -> assume docs are being updated, pass through
if [ -n "$DOC_FILES" ]; then
  exit 0
fi

# Determine which doc files need updating based on changed source paths
DOCS_TO_UPDATE=""

# Architecture: new/moved packages, domain models, ports, adapters, events, commands, queries
if echo "$SRC_FILES" | grep -qE "(domain/|application/|api/command/)"; then
  DOCS_TO_UPDATE="${DOCS_TO_UPDATE}- .claude/rules/architecture.md (domain/application/api structure changes)\n"
fi

# Coding conventions: DI wiring, serialization, naming patterns, condition rules
if echo "$SRC_FILES" | grep -qE "(koin\.kt|ConditionRuleJson|condition/|infrastructure/)"; then
  DOCS_TO_UPDATE="${DOCS_TO_UPDATE}- .claude/rules/coding-conventions.md (infrastructure/convention changes)\n"
fi

# Testing: test files, test infrastructure
if echo "$SRC_FILES" | grep -qE "(src/test/|Test\.kt$|AbstractClickHouseTest)"; then
  DOCS_TO_UPDATE="${DOCS_TO_UPDATE}- .claude/rules/testing.md (test changes)\n"
fi

# CLAUDE.md: new event types, new packages in architecture tree, env vars, new condition rules
if echo "$SRC_FILES" | grep -qE "(event/|condition/|rabbitmq/|clickhouse/.*\.sql)"; then
  DOCS_TO_UPDATE="${DOCS_TO_UPDATE}- CLAUDE.md (events, condition rules, or schema changes)\n"
fi

# Fallback: if source changed but no specific category matched, suggest reviewing CLAUDE.md
if [ -z "$DOCS_TO_UPDATE" ]; then
  DOCS_TO_UPDATE="- CLAUDE.md and/or .claude/rules/ files as appropriate\n"
fi

# Output reminder to stderr and block
cat >&2 <<EOF
Source files were modified but project documentation was not updated.
Please review and update these documentation files if the changes affect documented patterns:

$(echo -e "$DOCS_TO_UPDATE")
Changed source files:
$SRC_FILES

Only update docs where the changes actually affect documented content (architecture, conventions, patterns, event types, etc.). If the changes are minor and don't affect any documented content, you may add a brief comment to acknowledge this.
EOF

exit 2
