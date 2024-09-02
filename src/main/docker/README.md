```shell
sudo docker build --no-cache --progress=plain --secret id=gh_user,src=GH_USER.txt --secret id=gh_token,src=GH_TOKEN.txt -f src/main/docker/Dockerfile.multistage -t ghcr.io/pagopa/mil-auth:latest .
```