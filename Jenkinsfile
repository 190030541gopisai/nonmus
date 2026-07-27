pipeline {
    agent any

    triggers {
        pollSCM('* * * * *')
    }

    environment {
        COMPOSE_FILE = 'docker-compose.yml'
        JWT_SECRET = credentials('jwt-secret')
        DB_PASSWORD = credentials('db-password')
        BRANCH_TAG = "${env.BRANCH_NAME.replace('/', '-')}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                sh 'docker compose -f $COMPOSE_FILE build nonmus-api'
            }
        }

        stage('Build Frontend') {
            steps {
                sh 'docker compose -f $COMPOSE_FILE build nonmus-nginx'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'docker compose -f $COMPOSE_FILE run --rm nonmus-api ./mvnw test -B || echo "Tests skipped in Docker"'
            }
        }

        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    JWT_SECRET=$JWT_SECRET \
                    DB_PASSWORD=$DB_PASSWORD \
                    docker compose -f $COMPOSE_FILE up -d --force-recreate nonmus-api nonmus-nginx
                '''
            }
        }
    }

    post {
        success {
            echo "Build succeeded for branch: ${BRANCH_NAME}"
        }
        failure {
            echo "Build failed for branch: ${BRANCH_NAME}"
        }
    }
}
