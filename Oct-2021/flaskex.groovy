properties([
    parameters([
        string(description: 'Please enter HOST IP: ', name: 'HOSTIP', trim: true)
    ])
])

if( params.HOSTIP.length() >= 7 ) {
    node {
        stage("Pull Ansible Repo"){
            git 'https://github.com/ikambarov/ansible-Flaskex.git'
        }
        
        stage("Run Ansible"){
            withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
                withEnv(['FLASKEX_REPO=https://github.com/ikambarov/Flaskex.git', 'FLASKEX_BRANCH=master']) {
                    ansiblePlaybook credentialsId: 'jenkins-master-key', disableHostKeyChecking: true, inventory: "${params.HOSTIP},", playbook: 'main.yml'
                }
            }
        }
    }
}
else {
    error 'Please enter valid IP'
}
