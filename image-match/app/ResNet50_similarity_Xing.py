# -*- coding: utf-8 -*-
"""
Created on Mon Mar 30 12:46:51 2020
@author: x.liang@greenwich.ac.uk
Image Similarity using ResNet50
"""
import sys, os
import json

import tracemalloc
import psutil
import uuid
import gc
import numpy as np
from resnet50 import ResNet50
from keras.layers import Input
import keras.utils as image
from keras.applications.imagenet_utils import preprocess_input
from sklearn.metrics.pairwise import cosine_similarity
from flask import Flask, request, jsonify
from apiflask import HTTPError
from os.path import exists
import yaml
import urllib.parse
# avg_pool (AveragePooling2D) output shape: (None, 1, 1, 2048)
# Latest Keras version causing no 'flatten_1' issue; output shape:(None,2048) 
def get_feature_vector_fromPIL(img):
 feature_vector = feature_model.predict(img)
 a, b, c, n = feature_vector.shape
 feature_vector= feature_vector.reshape(b,n)

 return feature_vector

def calculate_similarity_cosine(vector1, vector2):
 return cosine_similarity(vector1, vector2)


def load_img(file, filename):
 image_path = config['storage']['path'] + filename
 file.save(image_path)
 img = image.load_img(image_path,target_size=(224, 224))
 os.remove(image_path)
 x = image.img_to_array(img)
 x = np.expand_dims(x, axis=0)
 x = preprocess_input(x)
 return x
def load_local_img(filename):
 img = image.load_img(filename,target_size=(224, 224))
 x = image.img_to_array(img)
 y = np.expand_dims(x, axis=0)
 z = preprocess_input(y)
 del(y)
 del(x)
 del(img)
 gc.collect()

 return z

def get_full_path(url):
 return config['storage']['linkPath'] + url


config_path = './config/application.yaml'
second_path = '/app/config/application-image-similarity.yaml'

def load_config():
 if exists(config_path):
  config = yaml.safe_load(open(config_path))
 if exists(second_path):
  config = yaml.safe_load(open(second_path))
 return config
# Use ResNet-50 model as an image feature extractor
image_input = Input(shape=(224, 224, 3))
feature_model = ResNet50(input_tensor=image_input, include_top=False,weights='imagenet')
config = load_config()
app = Flask(__name__)

process = psutil.Process(os.getpid())
tracemalloc.start()


@app.route("/", methods=['POST'])
def similarity():
 file = request.files['images1']
 file2 = request.files['images2']
 sys.stdout = open(os.devnull, 'w')
 if not file or not file2:
  raise HTTPError(400, message='Something is wrong...')
 image_similarity = calculate_similarity_cosine(
  get_feature_vector_fromPIL(load_img(file, str(uuid.uuid1()))),
  get_feature_vector_fromPIL(load_img(file2, str(uuid.uuid1()))))
 sys.stdout = sys.__stdout__
 return jsonify({
  "similarity": image_similarity[0][0]*100
 })

@app.route("/", methods=['GET'])
def similarity_local_file():
 file = request.args.get('images1')
 file2 = request.args.get('images2')
 print(get_full_path(file), exists(get_full_path(file)))
 before = psutil.virtual_memory()[3]/1000000
 if not file or not file2 or not exists(get_full_path(file)) or not exists(get_full_path(file2)):
  raise HTTPError(400, message='Something is wrong...')
 sys.stdout = open(os.devnull, 'w')
 img1 = load_local_img(get_full_path(file))

 img2 = load_local_img(get_full_path(file2))
 pil = get_feature_vector_fromPIL(img1)
 pil2 = get_feature_vector_fromPIL(img2)
 image_similarity = calculate_similarity_cosine(pil, pil2)
 similarity = image_similarity[0][0]*100
 sys.stdout = sys.__stdout__

 return jsonify({
  "similarity": similarity
 })