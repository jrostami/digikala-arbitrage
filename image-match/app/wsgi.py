from flask.cli import FlaskGroup

from ResNet50_similarity_Xing import app


cli = FlaskGroup(app)


if __name__ == "__main__":
    app.run()