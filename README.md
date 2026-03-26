# Cloud-Based Video CDN (Content Delivery Network)

A distributed system built with **Spring Boot** and **AWS S3** that simulates a real-world Content Delivery Network. This project demonstrates how to route traffic, handle large video files, and scale backend services.

## 🏗 System Architecture
The system consists of three main components working together:

1.  **CDN Router (This Repo):** Acts as a Load Balancer using a Round-Robin algorithm to distribute client requests.
2.  **Origin Server 1:** A storage node fetching content from AWS S3 and GitHub.
3.  **Origin Server 2:** A secondary storage node to demonstrate horizontal scaling and high availability.



## 🛠 Features
* **Custom Load Balancing:** Implements logic to rotate requests between available Origin Servers.
* **Multi-Source Streaming:** Streams video data from **AWS S3 Buckets** and **GitHub Raw** storage.
* **Byte-Range Support:** Enables seeking in video players by requesting specific parts of a file.
* **Health Checks:** The Router monitors the status of Origin Servers to ensure 100% uptime.

## 🔗 Project Components
To see the full implementation, visit the individual component repositories:
* [Origin Server 1](https://github.com/Chintanjkfp/cdn-origin-server-1) - Port 8085
* [Origin Server 2](https://github.com/Chintanjkfp/cdn-origin-server-2) - Port 8086

## 🚀 How to Run Locally
1.  **Start Origin Servers:** Run both Origin 1 and Origin 2 on their respective ports.
2.  **Start the Router:** Run this project on Port 8080.
3.  **Request Content:** Access `http://localhost:8080/router/stream/{video-key}`.
4.  **Observe Scaling:** Watch the logs to see the Router switch between Origin 1 and Origin 2 for every request.