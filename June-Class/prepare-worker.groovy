node {
    stage("Initialize") {
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {
            sh "ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@159.203.117.71 yum install epel-release -y "
        }
    }
}