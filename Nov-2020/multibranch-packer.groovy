node {
    def aws_region_var = ''
    def environment = ''
    def ami_name = ''

    if(env.BRANCH_NAME ==~ "dev.*"){
        println("Applying for dev")
        environment = "dev"
        aws_region_var = 'us-east-1'
    }
    else if(env.BRANCH_NAME ==~ "qa.*"){
        println("Applying for qa")
        environment = "qa"
        aws_region_var = 'us-east-2'
    }
    else if(env.BRANCH_NAME ==~ "master"){
        println("Applying for prod")
        environment = "prod"
        aws_region_var = 'us-west-2'
    }

    stage("Pull Repo"){
        checkout scm
    }

    ami_name = "apache-${UUID.randomUUID().toString()}"

    withCredentials([usernamePassword(credentialsId: 'jenkins-aws-access-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${aws_region_var}", "PACKER_AMI_NAME=${ami_name}"])  {
            stage("Packer Validate"){
                sh """
                    packer validate apache.json
                """        
            }
            stage("Packer Build"){
                sh """
                    packer build apache.json 
                """
            }
        }
    }

    stage("Update EC2 isntance"){
        build job: 'terraform_ec2', parameters: [
            booleanParam(name: 'terraform_apply', value: true),
            booleanParam(name: 'terraform_destroy', value: false),
            string(name: 'environment', value: "${environment}"),
            string(name: 'ami_name', value: "${ami_name}")
            ]
    }
}