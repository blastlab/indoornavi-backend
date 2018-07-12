#!/usr/bin/env bash

USER=dev
HOST=172.16.170.20
DESTINATION=/home/dev/indoor_navi

base_dir="$PWD"
apps=(backend frontend liquibase)

go_to_app_directory() {
	if [ $1 = backend ] ; then
		cd ${base_dir}
	elif [ $1 = frontend ] ; then
		cd ${base_dir}/../frontend
	elif [ $1 = liquibase ] ; then
		cd ${base_dir}/database
	fi
}

echo Please, provide next version...

read version

git tag -a ${version} -m "Version ${version}"
git push origin ${version}

docker login indoornavi.azurecr.io -u indoornavi -p JeLv55Xn87DA8tBR=2suAs9blMgXaDDD

for app in ${apps[*]}
	do
		go_to_app_directory ${app}
		docker build -t ${app}:${version} .
		docker tag ${app}:${version} indoornavi.azurecr.io/${app}:latest
		docker tag ${app}:${version} indoornavi.azurecr.io/${app}:${version}
		docker push indoornavi.azurecr.io/${app}:latest
		docker push indoornavi.azurecr.io/${app}:${version}
	done

go_to_app_directory backend

# copy docker compose
scp docker-compose.production.yml ${USER}@${HOST}:${DESTINATION}/docker-compose.yml

# copy liquibase changelogs
scp -r database/src/main/resources/ ${USER}@${HOST}:${DESTINATION}/database/src/main/

# copy war
scp deployments/serviceblbnavi-1.0.war ${USER}@${HOST}:${DESTINATION}/deployments