import numpy as np
from math import ceil


class HeatMapSolver:
    _gradient_coords = None
    _pix_coords = None
    _max_allowed = 10
    _divider = 1

    def __init__(self, max_gradients_num, map_x_length, map_y_length, scale_pix_x, scale_pix_y, distance_in_cm):
        if map_x_length > map_y_length:
            self._gradient = map_x_length / max_gradients_num
            self._gradients_in_x = round(max_gradients_num)
            self._gradients_in_y = round(map_y_length / self._gradient)
        else:
            self._gradient = map_y_length / max_gradients_num
            self._gradients_in_y = round(max_gradients_num)
            self._gradients_in_x = round(map_x_length / self._gradient)
        self._scale_cm_to_pix_x = scale_pix_x
        self._scale_cm_to_pix_y = scale_pix_y
        self._max_x_length = map_x_length
        self._max_y_length = map_y_length
        self._generate_tensor()

    def calculate_heat_map(self, data_payload):
        """Calculate tensor for heat map occurrence value
        corresponding to coordinates in pixels tensor
        :data_payload: given set of points to be mapped in to occurrence tensor
        :return: [[grads in x dir, grads in y dir], [x gradient num, y gradient num, occurrence]] which is a heat map tensor
        """
        for payload in data_payload:
            x_pix = payload['point']['x'] * self._scale_cm_to_pix_x
            y_pix = self._max_y_length - payload['point']['y'] * self._scale_cm_to_pix_y
            if self._is_valid_coordinate(x_pix, y_pix):
                indexes = ((self._pix_coords[:, 0] > x_pix) & (self._pix_coords[:, 1] > y_pix)).nonzero()
                if indexes[0].size > 0:
                    index = np.amin(indexes)
                    self._gradient_coords[index][2] += 1
        self._heat_median = np.mean(self._gradient_coords, axis=0)[2]
        self._divider = self._heat_median * 2 / self._max_allowed
        np.apply_along_axis(self._recalculate_occurrence_distribution_mean, 1, self._gradient_coords)
        print('*************************')
        print('Heat map has been solved.')
        print('*************************')
        return {
            "size": [self._gradients_in_x, self._gradients_in_y],
            "gradient": self._gradient_coords.tolist()
        }

    def _is_valid_coordinate(self, coord_x, coord_y, ):
        """Check if coordinates are in proper range
        We do this as there is no decision at this point
        what to do with received coordinates that are out of map range
        TODO: implement solution according to project owner opinion of it
        :coord_x: coordinate x represented in pixels to be compared with x map in pixels size
        :coord_y: coordinate y represented in pixels to be compared with y map in pixels size
        :return: bool, True if coordinates are in map range, False otherwise
        """
        x_ok = coord_x < self._max_x_length and coord_x > 0
        y_ok = coord_y < self._max_y_length and coord_y > 0
        return x_ok and y_ok

    def _generate_tensor(self):
        """Generates Table Tensor of size [X grad * Y grad][2]
        """
        _gradient_coords = list()
        _pix_coords = list()
        for i in range(0, self._gradients_in_x + 1):
            for j in range(0, self._gradients_in_y + 1):
                _gradient_coords.append([i, j, 0])
                _pix_coords.append([i * self._gradient, j * self._gradient])
        self._gradient_coords = np.array(_gradient_coords)
        self._pix_coords = np.array(_pix_coords)

    def _recalculate_occurrence_distribution_mean(self, arr):
        """Flattens occurrence value according to mean value approach
        :arr: array to apply flattening to
        """
        if arr[2] > 0:
            if arr[2] < self._divider:
                arr[2] = 1
            elif arr[2] > self._heat_median * self._max_allowed - self._heat_median:
                arr[2] = self._max_allowed
            else:
                arr[2] = ceil(arr[2] / self._divider) / 2
