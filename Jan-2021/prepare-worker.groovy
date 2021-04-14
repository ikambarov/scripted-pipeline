node{
    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-ssh-key', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {
        stage("Install git"){
            sh """
                ssh -o StrictHostKeyChecking=no -i $SSHKEY root@104.236.193.148 yum install git -y
            """
        }   
        stage("Install Java"){
            sh """
                ssh -o StrictHostKeyChecking=no -i $SSHKEY root@104.236.193.148 yum install java-1.8.0-openjdk-devel -y
            """
        }   
    }
}

