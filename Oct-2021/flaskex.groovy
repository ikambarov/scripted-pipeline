properties([
    parameters([
        string(description: 'Please enter HOST IP: ', name: 'HOSTIP', trim: true)
    ])
])

node {
    stage("Pull Ansible Repo"){
        git 'https://github.com/ikambarov/ansible-Flaskex.git'
    }
    
    stage("Run Ansible"){
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
            withEnv(['FLASKEX_REPO=https://github.com/ikambarov/Flaskex.git', 'FLASKEX_BRANCH=master']) {
                sh """
                    export ANSIBLE_HOST_KEY_CHECKING=False
                    ansible-playbook --private-key $SSH_KEY  -i '${params.HOSTIP},'  main.yml -u $SSH_USER
                """
            }
        }
    }
}