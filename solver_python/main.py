from flask import Flask, request
from flask_restful import reqparse, Api, Resource
from flask_cors import CORS
import requests
from solvers import HeatMapSolver

BASIC_URL = 'http://core:8080/rest/v1'
HEADERS = {
    "Authorization": "Token TestAdmin",
    "Accept": "application/json",
    "Content-Type": "application/json"
}

app = Flask(__name__)
CORS(app, origins="*", allow_headers=[
    "Content-Type", "Authorization", "Access-Control-Allow-Credentials"],
     supports_credentials=True)
api = Api(app)
parser = reqparse.RequestParser()


class CoordinatesHeatMap(Resource):
    def post(self):
        global HEADERS
        req = request.json
        args = {
            "from": req['from'],
            "to": req["to"],
            "floorId": req['floorId']
        }
        print(args)
        resp = requests.post(BASIC_URL + '/reports/coordinates/', headers=HEADERS,
                             json=args)
        heat_map_tensor = {
            "gradient": [],
            "size": []
        }
        if resp.status_code in range(200, 202):
            data = resp.json()
            max_gradients_num = req['maxGradientsNum']
            map_x_length = req['mapXLength']
            map_y_length = req['mapYLength']
            scale_pix_x = req['scaleInX']
            scale_pix_y = req['scaleInY']
            distance_in_cm = req['distanceInCm']
            solver = HeatMapSolver(max_gradients_num, map_x_length, map_y_length, scale_pix_x, scale_pix_y,
                                   distance_in_cm)
            heat_map_tensor = solver.calculate_heat_map(data)
        else:
            print("Server responded with code: {}.".format(resp.status_code))
        return heat_map_tensor


class HomePage(Resource):
    def get(self):
        return 'Solver Server is Working...'


api.add_resource(CoordinatesHeatMap, '/reports/heatmap/')
api.add_resource(HomePage, '/')

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=8080)
