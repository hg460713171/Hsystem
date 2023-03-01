# 如何搭建一个高可用的Java Web系统

## 整体架构介绍
one java web system consist of java back-end project and font-end project and DB as well as other middleware(e.g ramDB,mq,mircoservice middleware)
I worked as a software engineer in China for 4 years.Now I want to share some experience. Even through it may not be exactly correct both in my essay and my english lol.
let's assume that we have a vey simple architecture. how do we develop it to a highly available system
### simple architecture：
At first, we should achieve this simple architecture.
![1](images/architecture1.jpg)
#### first part---- font-end load balancer

##### introduce：
Typically, we have more two layer of font-end system to process
request. but we can divide it into two components.
Before that, I will introduce a concept:reverse proxy and the difference between proxy and reverse proxy is very simple.
> you can consider proxy as a VPN. you send request to vpn server and vpn server send request to your server
> But for reverse proxy,you send request to vpn server and then vpn server send request to reverse proxy. Finally, reverse proxy send request to your server.
- first component: 4-layer load balancer. 
> It's about how we can get a http request. 
Client click a button in browser. then A http1.1 request will be sent to DNS server. In some extent,DNS can help you do some 
>load balancer job and sent a http(s) request to real server which is our 4-layer load balancer.
> why it called 4-layer, because it only proxy tcp/udp in network layer. what the load balance do is just fetch the request and  
> change ip/port and then deliver it to next layer.
- second component: 7-layer load balancer.
> 4-layer load balancer will send request to 7-layer load balancer, typically it is last layer. 7-layer load balance
> is much  heavier than 4-layer ones. Because, it needs to parse the whole http message. and wrap it and establish a tcp link to 
> real back-end server
- why we use 7-layer load balancer.
> because it is more powerful , we can know what kind of http message it is and change some certain message in certain http request.
##### nginx：
we can choose nginx or F5  as our load balancer, but f5 is more expensive than nginx.
F5 is hardware.However,nginx is open-source software.
- download and install nginx(in docker enviroment)

````
docker pull nginx:version 
````
run container: notice the mount dir which you should replace directory with you own direction

the point is that   mkdir {log,conf,dist} and mount this three directory.

````
docker run --name=nginx --volume=/Users/hou/Desktop/sofeware_develop/nginx/logs:/var/log/nginx --volume=/Users/hou/Desktop/sofeware_develop/nginx/conf/nginx.conf:/etc/nginx/nginx.conf --volume=/Users/hou/Desktop/sofeware_develop/nginx/dist:/usr/share/nginx/dist -p 8086:80 --restart=no  --detach=true nginx
````
- nginx as 4-layer load balancer  
conf
````
stream {
    upstream yourname {
            server 172.16.1.5:80 weight=5 max_fails=3 fail_timeout=30s;
            server 172.16.1.6:80 weight=5 max_fails=3 fail_timeout=30s;
    }

    server {
            listen 80;
            proxy_connect_timeout 3s;
            proxy_timeout 3s;
            proxy_pass yourname;
    }
}
````
- nginx as 7-layer load balancer  
  conf
````
http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
    upstream yourname {
            server 172.16.1.5:80 weight=5 max_fails=3 fail_timeout=30s;
            server 172.16.1.6:80 weight=5 max_fails=3 fail_timeout=30s;
    }
    server {
        listen       80;
        # in docker you need change it to your ip addr
        server_name  127.0.0.1;
        root /usr/share/nginx/dist;
        location / {
            try_files $uri $uri/ /index.html;
            index  index.html index.htm;
        }
        location /webapp {
            proxy_pass  http://yourname;
        }
        # process 400 500 it's all depends
    }
    
````


