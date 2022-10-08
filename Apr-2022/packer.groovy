if( env.BRANCH_NAME == "dev" ) {
    region = "us-east-1"
    environment = "dev"
}
else if( env.BRANCH_NAME == "qa" ) {
    region = "us-east-2"
    environment = "qa"
}
else if( env.BRANCH_NAME == "master" ){
    region = "us-west-2"
    environment = "prod"
}
else {
    error 'Branch name does not match the naming convention'
}

ami_name = "apache-${UUID.randomUUID().toString()}"

template = '''
apiVersion: v1
kind: Pod
metadata:
  name: packer
spec:
  containers:
  - image: ikambarov/packer:v2
    name: packer
    '''

podTemplate(cloud: 'kubernetes', label: 'packer', showRawYaml: false, yaml: template) {
    node("packer") {
        container("packer") {
            stage("Pull Repo"){
                git "https://github.com/ikambarov/packer.git"
            }

            withCredentials([usernamePassword(credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
                withEnv(["AWS_REGION=${region}", "PACKER_AMI_NAME=${ami_name}"]) {
                    stage("Build") {
                        sh "packer build apache.json"

                        build job: 'terraform-ec2', parameters: [string(name: 'environment', value: "${environment}"), string(name: 'action', value: 'apply'), string(name: 'ami_name', value: "${ami_name}")]
                    }
                }
            }
        }
    }
}