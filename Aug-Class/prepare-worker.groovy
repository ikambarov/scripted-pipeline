node{
    stage('Initialize'){
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-ssh-key', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {
            sh '''
                    ssh -i $SSHKEY root@104.131.114.15 yum install epel-release -y
                '''
        }
    } 
}

