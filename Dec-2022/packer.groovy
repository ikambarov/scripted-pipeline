properties([
    parameters([
        choice(choices: ['dev', 'qa', 'prod'], description: 'Choose an Environment', name: 'environment'),
        ])
    ])

if( params.environment == 'dev' ) {
    vpc_region = "us-east-1"
}

else if( params.environment == 'qa' ) {
    vpc_region = "us-east-2"
}

else {
    vpc_region = "us-west-2"
}

node {
    stage("Pull Repo"){
        git 'https://github.com/ikambarov/packer.git'
    }
    
    def ami_name = "apache-${UUID.randomUUID().toString()}"
    withCredentials([usernamePassword(credentialsId: 'aws-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${vpc_region}", "PACKER_AMI_NAME=${ami_name}"]) {
            stage("Validate"){
                sh """
                    packer validate apache.json
                """
            }

            stage("Build"){
                sh """
                    packer build apache.json
                """

                build job: 'terraform-ec2', parameters: [
                                                            string(name: 'environment', value: "${params.environment}"),
                                                            string(name: 'ami', value: "${ami_name}"),
                                                            string(name: 'action', value: 'apply')
                                                            ]
            }
        }
    }
}