node("ansible"){
    stage("Pull Repo"){
        git 'https://github.com/ikambarov/ansible-Flaskex.git'
    }
    stage("Install Prerequisites"){
        ansiblePlaybook credentialsId: 'ansible-key', disableHostKeyChecking: true, inventory: "159.65.39.11,", playbook: 'prerequisites.yml'
    }
    withEnv(['FLASKEX_REPO=https://github.com/ikambarov/Flaskex.git', 'FLASKEX_BRANCH=master']) {
        stage("Pull Repo"){
            ansiblePlaybook credentialsId: 'ansible-key', disableHostKeyChecking: true, inventory: "159.65.39.11,", playbook: 'pull_repo.yml'
        }
    }    
    stage("Install Python"){
        ansiblePlaybook credentialsId: 'ansible-key', disableHostKeyChecking: true, inventory: "159.65.39.11,", playbook: 'install_python.yml'
    }
    stage("Start App"){
        ansiblePlaybook credentialsId: 'ansible-key', disableHostKeyChecking: true, inventory: "159.65.39.11,", playbook: 'start_app.yml'
    }
}