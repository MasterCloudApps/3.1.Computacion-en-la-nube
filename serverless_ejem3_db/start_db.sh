docker run -d -p 8000:8000 amazon/dynamodb-local

aws dynamodb create-table --table-name appusers \
  --attribute-definitions  AttributeName=userid,AttributeType=S \
  --key-schema AttributeName=userid,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --endpoint-url http://127.0.0.1:8000