properties([
    parameters([
        string(defaultValue: '', description: 'Input node IP', name: 'SSHNODE', trim: true)
        ])
    ])

node {
    stage("Initialize") {
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {
            sh "ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@$params.SSHNODE yum install epel-release -y "
        }
    }
}
