# WordCloud
An offline Android WordCloud application that showcases my WordCloudView. Current features: words, text size equations, skips, show word loading and circle radius.

## Getting Started
Download from the Google Play Store or
```
$ git clone https://github.com/kpchungdev/WordCloud
```
## Shown Features
### Words 
Creates a Word Cloud from an inputted string.
```
word: Hi Bye Bye Hi Hi word word Hi Bye Bye Bye word word Hi Hi
```

### Text Equation
Determines text size for each repetition; variable is "repetition".
```
f(x) = text size(px) = 2 * repetition + 10
```

### Skips
Loads Word Cloud immediately if skips limit is met at any repetition. Skips = non-inserted words
```
skips: 4 
rep = 12 -> 0 skips
rep = 7 -> 2 skips
rep = 4 -> 3 skips
rep = 2 -> 4 skips -> Word Cloud is loaded immediately
```

### Show Words Loading
Replaces progress bar for loading with words phasing in.
```
Show Words Loading: False -> shows progress bar
Show Words Loading: True -> hides progress bar and phases in words per repetition.
```

### Circle Radius
Morphs Word Cloud to a circle with given radius; radius is capped at your device's lowest dimension.
```
Device has a width of 100 and height of 50.
Radius is 20.
Everything is fine.

Device has a width of 100 and height of 50.
Radius is 200.
Radius changes to 25.
```

## Unshown Features

### Unwanted Words
Deletes unwanted words
```
Unwanted Words: The, What, @
```

### Joiners
Deletes characters that join words together.
```
joiners: ",", "!", "?", "@"...
```

### Typeface
To change font, change typeface.
```
typeface = Bevan
```

### Colors
To change colors, input another color array
```
colors = [red, blue, green, yellow, blue];
```

### Default Colors
After all colors are used, default color is used.
```
colors = black;
```

### Paddings
Paddings shift the wordcloud's midpoint.
```
padding[left, top, right, bottom]
before: midpoint = (width / 2, height / 2)
after: midpoint = ((width / 2) + left - right), ((height / 2) + top - bottom)
```

## Remarks
Feel free to take WordCloudView and use it for your own project.
