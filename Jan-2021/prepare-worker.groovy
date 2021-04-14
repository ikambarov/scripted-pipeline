node{
    stage("Install git"){
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-ssh-key', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {
            sh """
                ssh -o StrictHostKeyChecking=no -i $SSHKEY root@104.236.193.148 yum install git -y
            """
        }   
    }
}

