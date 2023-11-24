#!/usr/bin/env bash
set -ex

export AWS_ACCESS_KEY_ID="some_access_key1"
export AWS_SECRET_ACCESS_KEY="some_secret_key1"

aws --endpoint-url http://localhost:8333 s3 ls
aws --endpoint-url http://localhost:8333 s3 mb s3://test-bucket
aws --endpoint-url http://localhost:8333 s3 ls