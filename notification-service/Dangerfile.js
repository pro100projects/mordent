import { danger, warn, fail } from "danger"

const SEMANTIC_COMMIT_TYPES = ["build", "chore", "ci", "docs", "feat", "fix", "perf", "refactor", "revert", "style", "test"];
const IGNORED_COMMIT_MESSAGES = ["Merge branch", "Revert \"", "chore: update snapshots"];
const COMMIT_SUBJECT_MAX_LENGTH = 72
const COMMIT_BODY_MAX_LENGTH = 144

function match(text) {
    return text.match(/^(?<type>\\w+)(?:\\((?<scope>.+?)\\))?:(?<description>.+?)$/);
}

if(match(danger.gitlab.mr.title)) {
    fail("The MR title does not comply with conventional commits specifications.")
} else if(!SEMANTIC_COMMIT_TYPES.find(type => danger.gitlab.mr.title.startsWith(type))) {
    fail("The MR title type is not a well-known semantic commit type.");
}

for (const commit of danger.gitlab.commits) {
    if(IGNORED_COMMIT_MESSAGES.find(type => commit.title.startsWith(type))) {
        continue;
    }

    if(match(commit.title)) {
        fail(`${commit.id} - The commit title does not comply with conventional commits specifications.`);
    } else if(!SEMANTIC_COMMIT_TYPES.find(type => commit.title.startsWith(type))) {
        fail(`${commit.id} - The commit title type is not a well-known semantic commit type.`);
    }

    if(commit.title.length > COMMIT_SUBJECT_MAX_LENGTH) {
        fail(`${commit.id} - The commit subject may not be longer than ${COMMIT_SUBJECT_MAX_LENGTH} characters.`);
    }
    if(commit.message.length > COMMIT_BODY_MAX_LENGTH) {
        fail(`${commit.id} - The commit subject may not be longer than ${COMMIT_SUBJECT_MAX_LENGTH} characters.`);
    }

    if(commit.title.endsWith(".")) {
        fail(`${commit.id} - The commit subject must not end with a period.`);
    }
}

if (danger.gitlab.commits.length > 10) {
    warn('This merge request includes more than 10 commits. Please rebase these commits into a smaller number of commits.');
}