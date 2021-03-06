# Spring cloud aws sample

This is a sample application for demonstration purposes only. It is a very simple application devoted to managing advertisements (actually, it doesn't allow deletion and update of ads). The idea is demonstrate Spring Cloud AWS capabilities. In order to run it, you need a minimum AWS configuration (see below).

The application allows users to create ads with a picture. Ads are stored in a RDS database in AWS. The project is configured to use MySQL, but that can be changed to use a different database (for instance, PostgreSQL). Pictures within ads are stored in a bucket in S3.

## Prerequisites

### Create an RDS Instance

**WARNING**: These instructions allow you to run and test the application from within your development environment (i.e., without deploying it to AWS) using an RDS instance open to the world, which is something you should avoid in production. 

First, create a _security group_ that will be used to allow ingress connections from outside AWS. Whithin the security group just created, create a new _access rule_ with the following configuration:

* Type: MySQL/Aurora
* Source: Anywhere
* CIDR: 0.0.0.0/0
  
Then, create an RDS instance, with these properties:

* Engine MySQL
* Type dev/test
* DB Instance Class: t2.micro
* Multizone AZ: no
* DB Instance identifier: springaws (we will provide this as app argument cloud.aws.rds.dbInstanceIdentifier)
* Master username: springaws (we will provide this as app argument cloud.aws.rds.springaws.username)
* Master password: <your turn> (we will provide this as app argument cloud.aws.rds.springaws.password)
* Ensure Default VPC is enabled
* Ensure Publicly accessible is yes
* VPC security group: choose the security group previously created
* Database name: springaws (you'll need to open the _advanced settings_ in order to see this option)
* Launch!!

### Create an S3 bucket

Finally, create an S3 bucket, name it `spring-cloud-aws-sample` and give it read permissions to anonymous users. Just copy and paste this aws policy to enable anonymous read access:

	{
	  "Version":"2012-10-17",
	  "Statement":[
	    {
	      "Sid":"AddPerm",
	      "Effect":"Allow",
	      "Principal": "*",
	      "Action":["s3:GetObject"],
	      "Resource":["arn:aws:s3:::spring-cloud-aws-sample/*"]
	    }
	  ]
	}

### Create an IAM role

Create an IAM role with the following properties:

* EC2 role (i.e., a role to be attached to EC2 instances)
* Policies:
** AmazonS3FullAccess
** AmazonRDSFullAccess

### Create an EC2 instance

It has been tested with an instance with the following properties:

* AMI: Ubuntu 20.04
* Type: t2.micro
* Storage: 20Gb
* Security group: choose or create one with ports 22 and 8080 opened
* Attach the IAM role created previously

### Deploy the application

Once the instance has been started, ssh'd into the machine and issue the following commands:

```
sudo apt update
sudo apt install openjdk-14-jre-headless
``` 

Then from your own machine, build the jar file and upload it to your EC2 instance:

```
mvn package -DskipTests
scp -i <your key> spring-cloud-aws-sample-0.2.0.jar ubuntu@<your ec2 ip>:/home/ubuntu/
```

## Run the application

If your EC2 instance has the appropriate role (see prerequisites above), and the jar file has been uploaded, the it can be run just by issuing:

    java -jar spring-cloud-aws-sample-0.2.0-SNAPSHOT.jar --cloud.aws.rds.db-instance-identifier="springaws" --cloud.aws.rds.springaws.password="your rds password"
 

   
