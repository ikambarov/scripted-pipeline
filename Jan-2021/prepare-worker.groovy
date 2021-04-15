properties([
    parameters([
        string(defaultValue: '', description: 'Enter IP Address', name: 'IPADDRESS', trim: true)
        ])
    ])

node{
    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-ssh-key', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {
        stage("Install git"){
            sh """
                ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${params.IPADDRESS} yum install git -y
            """
        }   
        stage("Install Java"){
            sh """
                ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${params.IPADDRESS} yum install java-1.8.0-openjdk-devel -y
            """
        }   
        stage("Install Ansible"){
            sh """
                ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${params.IPADDRESS} yum install epel-release -y
                ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${params.IPADDRESS} yum install ansible -y
            """
        }   
    }
}

