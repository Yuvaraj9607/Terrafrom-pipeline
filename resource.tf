resource "aws_s3_bucket" "dev060627" {
   bucket = "dev060625"
   tags = {
    name = "devbucket"
   }
}

resource "aws_instance" "Production" {

ami = var.ami
instance_type = var.instance_type
availability_zone = var.availability_zone

tags = {
    Name = "Jenkins Pipeline"
    Environment = "Production"
  }
}



