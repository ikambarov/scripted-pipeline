properties([
    parameters([
        choice(choices: ['dev', 'qa', 'prod'], name: 'environment')
        ])
    ])

if( params.environment == "dev" ) {
    region = "us-east-1"
}
else if( params.environment == "qa" ) {
    region = "us-east-2"
}
else {
    region = "us-west-2"
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
                    }
                }
            }
        }
    }
}