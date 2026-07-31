docker compose up -d

floci start

aws s3api create-bucket \
    --bucket nonmus-profile-pics \
    --region us-east-1

aws s3api create-bucket \
    --bucket nonmus-channel \
    --region us-east-1

aws s3api put-bucket-cors \
  --bucket nonmus-profile-pics \
  --cors-configuration file://cors.json

aws s3api get-bucket-cors --bucket nonmus-profile-pics

aws s3 cp src/main/resources/static/default_profile_picture.png s3://nonmus-profile-pics/users/default-avatar.png