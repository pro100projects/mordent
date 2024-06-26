#!/bin/bash
# Copyright 2021 "Holloway" Chew, Kean Ho <kean.ho.chew@zoralab.com>
# Copyright 2020 Benny Powers (https://forum.gitlab.com/u/bennyp/summary)
# Copyright 2017 Adam Boseley (https://forum.gitlab.com/u/adam.boseley/summary)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


##############
# user input #
##############
# project ID (Help: goto "Settings" > "Generals")
projectID="626"

# user API token (Help: "User Settings" > "Access Tokens" > enable "API")
token=$GITLAB_TOKEN

# gitlab server instance (E.g. 'gitlab.com')
server="hub.teamvoy.com"

# CI Jobs pagninations (Help: "CI/CD" > "Jobs" > see bottom pagnination bar)
#
# NOTE: user interface might be bug. If so, you need to manually calculate.
# Example:
#   1. For 123 jobs in the past, per_page is "100", it has 2 pages in total
#      [Pages = ROUND_UP(123 / 100)].
start_page="1"
end_page="10"
per_page="20"

# GitLab API version
api="v4"

#####################
# internal function #
#####################
delete() {
        # page
        page="$1"
        1>&2 printf "Cleaning page ${page}...\n"

        # build internal variables
        baseURL="https://${server}/api/${api}/projects"

        # get list from servers for the page
        url="${baseURL}/${projectID}/jobs/?page=${page}&per_page=${per_page}"
        1>&2 printf "Calling API to get lob list: ${url}\n"

        list=$(curl --globoff --header "PRIVATE-TOKEN:${token}" "$url" \
                | jq -r ".[].id")
        if [ ${#list[@]} -eq 0 ]; then
                1>&2 printf "list is empty\n"
                return 0
        fi

        # remove all jobs from page
        for jobID in ${list[@]}; do
                url="${baseURL}/${projectID}/jobs/${jobID}/erase"
                1>&2 printf "Calling API to erase job: ${url}\n"

                curl --request POST --header "PRIVATE-TOKEN:${token}" "$url"
                1>&2 printf "\n\n"
        done
}

main() {
        # check dependencies
        if [ -z $(type -p jq) ]; then
                1>&2 printf "[ ERROR ] need 'jq' dependency to parse json."
                exit 1
        fi

        # loop through each pages from given start_page to end_page inclusive
        for ((i=start_page; i<=end_page; i++)); do
                delete $i
        done

        # return
        exit 0
}
main $@j
