# -*- coding: utf-8 -*-
"""
x.liang@greenwich.ac.uk
25th March, 2020
Image Similarity using VGG16
"""
import os
import uuid
import numpy as np
from keras.layers import Input
from keras.models import Model
from vgg16 import VGG16
import keras.utils as image
from keras.applications.imagenet_utils import preprocess_input
from sklearn.metrics.pairwise import cosine_similarity
from flask import Flask, request, jsonify
from apiflask import HTTPError
'''
def get_feature_vector(img):
 img1 = cv2.resize(img, (224, 224))
 feature_vector = feature_model.predict(img1.reshape(1, 224, 224, 3))
 return feature_vector
'''

# fc2(Dense)output shape: (None, 4096) 
def get_feature_vector_fromPIL(img):
 feature_vector = feature_model.predict(img)
 assert(feature_vector.shape == (1,4096))
 return feature_vector

def calculate_similarity_cosine(vector1, vector2):
 #return 1- distance.cosine(vector1, vector2)
 return cosine_similarity(vector1, vector2) 

# This distance can be in range of [0,∞]. And this distance is converted to a [0,1]
def calculate_similarity_euclidean(vector1, vector2):
 #return distance.euclidean(vector1, vector2)     #distance.euclidean is slower
 return 1/(1+np.linalg.norm(vector1 - vector2))   #np.linalg.norm is faster

# Load images in the images folder into array
cwd_path = os.getcwd()
data_path =cwd_path + '/images/'
def load_img(file, filename):
 image_path = data_path + filename
 file.save(image_path)
 img = image.load_img(image_path,target_size=(224, 224))
 os.remove(image_path)
 x = image.img_to_array(img)
 x = np.expand_dims(x, axis=0)
 x = preprocess_input(x)
 return x


# Use VGG16 model as an image feature extractor 
image_input = Input(shape=(224, 224, 3))
model = VGG16(input_tensor=image_input, include_top=True,weights='imagenet')
layer_name = 'fc2'
feature_model = Model(inputs=model.input,outputs=model.get_layer(layer_name).output)


app = Flask(__name__)
@app.route("/", methods=['POST'])
def similarity():
 file = request.files['images1']
 file2 = request.files['images2']
 if not file or not file2:
  raise HTTPError(400, message='Something is wrong...')

 image_similarity = calculate_similarity_cosine(
  get_feature_vector_fromPIL(load_img(file, str(uuid.uuid1()))),
  get_feature_vector_fromPIL(load_img(file2, str(uuid.uuid1()))))

 return jsonify({
  "similarity": image_similarity[0][0]*100
 })