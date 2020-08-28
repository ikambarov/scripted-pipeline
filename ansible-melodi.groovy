properties([
    parameters([
        string(defaultValue: '', description: 'Please enter VM IP', name: 'nodeIP', trim: true),
        string(defaultValue: '', description: 'Please enter branch name', name: 'branch', trim: true)
        ])
    ])

if (nodeIP?.trim()) {
    node {
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-ssh-key', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {
            stage('Pull Repo') {
                git branch: 'master', changelog: false, poll: false, url: 'https://github.com/ikambarov/ansible-melodi.git'
            }

            withEnv(['ANSIBLE_HOST_KEY_CHECKING=False']) {
                stage("Install Apache"){
                    sh 'ansible-playbook -i "${nodeIP}," -e "melodi_branch=${branch}" --private-key $SSHKEY main.yml'
                    }
                }  
            }  
    }
}
else {
    error 'Please enter valid IP address'
}


