from setuptools import setup

setup(
    name='nicoLase',
    version='0.1.0',
    author='Rusty Nicovich',
    author_email='rustyn@alleninstitute.org',
    packages=['nicoLase'],
    license='LICENSE.txt',
    description='PC-side drivers for nicoLase Arduino shield',
    long_description=open('README.txt').read(),
    install_requires=[
        "serial",
    ],
)