# cat-detector
Neural network to detect a cat in a photo annd generate photos with a new cat

#### Questo progetto usa Git LFS per gestire immagini e file pesanti.
Per scaricare correttamente il progetto:
- Installare Git LFS: https://git-lfs.github.com/
- Clonare il repository normalmente
- Eseguire: git lfs pull

#### Su Visual Studio Code

Bisogna aggiungere in file 'launch.json' così strutturato:
```
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Current File",
            "request": "launch",
            "mainClass": "${file}"
        },
        {
            "type": "java",
            "name": "CatDetector",
            "request": "launch",
            "mainClass": "neural_network/neural_network.CatDetector",
            "projectName": "Cat_detector",
            "env": {
                "PEXELS_API_KEY": "Your API KEY"
            }
        },
        {
            "type": "java",
            "name": "ImageDownloader",
            "request": "launch",
            "mainClass": "neural_network/neural_network.image.ImageDownloader",
            "projectName": "Cat_detector"
        }
    ]
}


```

e in file .classpath così strutturato

```
<?xml version="1.0" encoding="UTF-8"?>
<classpath>
	<classpathentry kind="src" path="src"/>
	<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER">
		<attributes>
			<attribute name="module" value="true"/>
		</attributes>
	</classpathentry>
	<classpathentry kind="lib" path="libraries/json-20230227.jar">
		<attributes>
			<attribute name="module" value="true"/>
		</attributes>
	</classpathentry>
	<classpathentry kind="output" path="bin"/>
</classpath>


```
