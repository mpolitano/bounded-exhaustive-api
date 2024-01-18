## Install in macOS running on Apple CPU

For *BEAPI* to run in Apple sillicon you might need to create a Docker container for the amd64 platform (if the default arm64 container does not work). This container runs in emulated mode in the Apple CPU, and might produce a significant performance hit and some issues.

Build a docker container using buildx [0]:

```
docker buildx create --name amd64builder
docker buildx use amd64builder
docker buildx build --platform linux/amd64 -t pli . --load
```

**Warning**:  The last command may take a long time.


Run the container:

```
docker run --platform linux/amd64 -it pli:latest /bin/bash
```


To run the ```NodeCachingLinkedList``` example follow the instruction in [Running example](README.md#running-example)

* * *

[0] https://docs.docker.com/desktop/multi-arch/

* * *

Go back to [Table of Contents](README.md)
