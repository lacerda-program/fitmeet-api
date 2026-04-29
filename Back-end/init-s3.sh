#!/bin/bash

# LocalStack S3 Bucket Initialization Script
# This script initializes the S3 bucket in LocalStack after containers start

echo "Waiting for LocalStack to be ready..."
sleep 10

echo "Creating S3 bucket in LocalStack..."

# Create the S3 bucket
docker exec bootcamp_localstack awslocal s3 mb s3://bootcamp-bucket

# Verify bucket creation
echo ""
echo "Verifying bucket creation..."
docker exec bootcamp_localstack awslocal s3 ls

echo ""
echo "S3 bucket initialization complete!"
echo ""
echo "Access S3 via AWS CLI:"
echo "  aws s3 ls s3://bootcamp-bucket --endpoint-url http://localhost:4566"
echo ""
echo "Or via Spring Boot (automatically configured in application.yaml)"

