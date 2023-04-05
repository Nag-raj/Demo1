import json
import requests
import time
import sys

build_mode = sys.argv[1]
api_token = '3d33fb50cbf33b60152ab7d7464cb5b4'
sub_code = '8b4fa19f19de48eead232e6063934df6'
api_url_base = 'https://portalrotapi.hana.ondemand.com/v2/subscriptions/{0}'.format(sub_code)
headers = {'Content-Type': 'application/json', 'Authorization': 'Bearer {0}'.format(api_token)}



def get_builds():

    print(api_url_base)
    api_url = '{0}/builds'.format(api_url_base)

    response = requests.get(api_url, headers=headers)

    if response.status_code == 200:
        #builds = json.loads(response.content)
        builds = response.json()
        print(json.dumps(builds, indent=2, sort_keys=False))


def create_build():

    api_url = '{0}/builds'.format(api_url_base)
    response = requests.post(api_url, data=json.dumps(build_params), headers=headers)
    time.sleep(30)

    if response.status_code == 201:
        #build_id = json.loads(response.content)
        build_id = response.json()
#       print(json.dumps(build_id, indent=2, sort_keys=False))
        get_build(build_id['code'])
        sys.stdout.write(build_id['code'])
    elif response.status_code == 401:
        sys.stdout.write("UNAUTHORIZED")
    else:
        sys.stdout.write("BUILD_API_ERROR")


def get_build(build_code):

    api_url = '{0}/builds/{1}'.format(api_url_base, build_code)

    while True:

        response = requests.get(api_url, headers=headers)

        if response.status_code == 200:
            #build_status = json.loads(response.content)
            build_status = response.json()
#           print("Build status is " + build_status['status'])

            if build_status['status'] == 'SUCCESS':
                break
            elif build_status['status'] == 'FAIL':
#               print("Build failed")
#               print("Build status is " + build_status['status'])
                sys.stdout.write("BUILD_FAILED")
                exit()
            else:
                time.sleep(30)


def get_build_progress(build_code):

    api_url = '{0}/builds/{1}/progress'.format(api_url_base, build_code)

    response = requests.get(api_url, headers=headers)

    if response.status_code == 200:
        #build_progress = json.loads(response.content)
        build_progress = response.json()
        print(json.dumps(build_progress, indent=2, sort_keys=False))


def get_build_logs(build_code):

    api_url = '{0}/builds/{1}/logs'.format(api_url_base, build_code)

    response = requests.get(api_url, headers=headers)

    if response.status_code == 200:
        #build_logs = json.loads(response.content)
        build_logs = response.json()

        print(json.dumps(build_logs, indent=2, sort_keys=False))


def create_deployment():

    api_url = '{0}/deployments'.format(api_url_base)
    response = requests.post(api_url, data=json.dumps(deploy_params), headers=headers)

    if response.status_code == 201:
        #build_id = json.loads(response.content)
        build_id = response.json()
        dep_code = build_id['code']

        get_deployment(dep_code)
    else:
        sys.stdout.write("Error response code received from the deployment API")



def get_deployment(dep_code):

    api_url = '{0}/deployments/{1}'.format(api_url_base, dep_code)

    while True:

        response = requests.get(api_url, headers=headers)

        if response.status_code == 200:
            #dep_progress = json.loads(response.content)
            dep_progress = response.json()

            if dep_progress['status'] == 'DEPLOYING':
#                print('Deployment in progress')
                time.sleep(30)
            else:
                print('Deployment status is ' + dep_progress['status'])
#               print(json.dumps(dep_progress, indent=2, sort_keys=False))
                break


def update_properties(env, propFile):
    propVal = '{ "key": "customer-properties", "value": "'
    api_url = '{0}/environments/{1}/services/hcs_common/properties/customer-properties'.format(api_url_base, env)

    with open(propFile, 'r') as fileObj:
        for curline in fileObj:
            if not (curline.startswith("#") or curline.startswith('\n')):
                propVal += curline.rstrip('\n')
                propVal += "\\n"

    propVal += '" }'
    #print(propVal)
    fileObj.close()

    response = requests.put(api_url, data=propVal, headers=headers)
    print(response.status_code)


################### START OF MAIN #######################
#get_builds()
#create_build()
#get_build(20200213.5)
#create_deployment()
#get_deployment()

if build_mode == 'build':
    branch = sys.argv[2]
    build_name = sys.argv[3]
    build_params = {"applicationCode": "commerce-cloud", "branch": branch, "name": build_name}

    create_build()
elif build_mode == 'deploy':
    buildCode = sys.argv[2]
    DBUpdate = sys.argv[3]
    env = sys.argv[4]
    strategy = sys.argv[5]
    deploy_params = {"buildCode": buildCode, "databaseUpdateMode": DBUpdate, "environmentCode": env, "strategy": strategy}

    create_deployment()
elif build_mode == 'update-properties':
    env = sys.argv[2]
    propFile = sys.argv[3]

    update_properties(env, propFile)
else:
    print('Invalid build mode')