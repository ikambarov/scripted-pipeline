properties([
    parameters([
        choice(choices: ['dev', 'qa', 'prod'], description: 'Choose an ENV from the list', name: 'environment')
        ])
    ])

if(params.environment == 'dev'){
    region="us-east-1"
}
else if(params.environment == 'qa'){
    region="us-east-2"
}
else{
    region="us-west-2"
}

node("worker1"){
    stage("Pull code"){
        git 'https://github.com/ikambarov/packer.git'
    }

    image_name="$params.environment-apache-${UUID.randomUUID().toString()}"

    withEnv(["AWS_REGION=$region", "PACKER_AMI_NAME=$image_name"]) {
        withCredentials([usernamePassword(credentialsId: 'aws-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {

            stage("Validate"){
                sh '''
                    packer validate apache.json
                '''
            }

            stage("Build"){
                sh '''
                    packer build apache.json
                '''
            }
        }
    }
    // stage("Create Instance"){
    //     build 'terraform-ec2'
    // }
}