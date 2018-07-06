package com.gmail.kpchungdev.wordcloud;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.gmail.kpchungdev.wordcloud.tutorial.WordCloudContract;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;


/*
 * If you would like to use wordcloudview for your own project,
 * omit all line with presenters and replace all default resources
*/

public class WordCloudView extends View {

    public static final String REPETITION_VARIABLE = "repetition";

    private static final String PROPERTY_OPACITY = "PROPERTY_OPACITY";

    public static final int DEFAULT_MINIMUM_TEXT_SIZE = 10;
    public static final int DEFAULT_ALLOWED_SKIPS = 10;

    private static final int QUADRANTS_FULL = -1;
    private static final int QUADRANT_1 = 0;
    private static final int QUADRANT_2 = 1;
    private static final int QUADRANT_3 = 2;
    private static final int QUADRANT_4 = 3;

    private static final int SIDE_BOTTOM = 0;
    private static final int SIDE_RIGHT = 1;
    private static final int SIDE_TOP = 2;
    private static final int SIDE_LEFT = 3;
    private static final int SIDE_EXIT = 4;

    private static final int DEFAULT_ANIMATION_TIME = 2500;

    private Context context;

    private WordCloudContract.UserActionListener presenter;

    private Point midpoint;
    private Random rand;

    private Paint textPaint;

    private AnimatorSet animatorSet;
    private PropertyValuesHolder opacityProperty;

    private Rect rectWord;
    private Rect rectPadding;
    private Rect spiral;

    private Expression textSizeExpression;

    private SparseArray<ArrayList<String>> repToWords;
    private SparseArray<Float> repToTextSize;

    private ArrayList<Integer> highestRepToLowestRepColors;
    private ArrayList<Integer> repAvailable;

    private ConcurrentHashMap<String, Point> verticalWordsPoints;
    private ConcurrentHashMap<String, Point> horizontalWordsPoints;
    private ConcurrentHashMap<String, Integer> wordToOpacity;

    private boolean[][] collision;

    private String[] joiners;
    private String[] unwantedWords;

    /* [0] = top right [1] = top left [2] = bottom left [3] = bottom right */
    private double[] estimateQuadrantSpaceAvailable;

    private String words;

    private float dipToPixelScale;
    private float minimumTextSize;

    private int width;
    private int height;
    private int xMax;
    private int xMin;
    private int yMax;
    private int yMin;
    private int circleRadius;
    private int allowedSkips;
    private int wordLeft;
    private int wordRight;
    private int wordTop;
    private int wordBottom;
    private int defaultColor;
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;

    private boolean loadingShowWords;
    private boolean loading;
    private boolean loadInit;
    private boolean finishedLoadingInit;
    private boolean reset;

    public WordCloudView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.rand = new Random();
        this.animatorSet = new AnimatorSet();
        this.opacityProperty = PropertyValuesHolder.ofInt(PROPERTY_OPACITY, 0, 225);
        this.repToWords = new SparseArray<>();
        this.repToTextSize = new SparseArray<>();
        this.highestRepToLowestRepColors = new ArrayList<>();
        this.repAvailable = new ArrayList<>();
        this.verticalWordsPoints = new ConcurrentHashMap<>();
        this.horizontalWordsPoints = new ConcurrentHashMap<>();
        this.wordToOpacity = new ConcurrentHashMap<>();
        this.estimateQuadrantSpaceAvailable = new double[4];
        this.dipToPixelScale = getContext().getResources().getDisplayMetrics().density;
        this.rectWord = new Rect(0, 0,0 ,0);
        this.spiral = new Rect(0, 0, 0, 0);
        this.reset = false;
        this.loading = false;
        this.finishedLoadingInit = false;

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.WordCloudView,
                0, 0);

        int fontPath = 0;
        int wordCloudColorsPath = 0;

        try {
            this.defaultColor = a.getInteger(R.styleable.WordCloudView_defaultColor, Color.BLACK);
            this.words = a.getString(R.styleable.WordCloudView_words);
            this.allowedSkips = a.getInteger(R.styleable.WordCloudView_allowedSkips, DEFAULT_ALLOWED_SKIPS);
            this.minimumTextSize = a.getDimension(R.styleable.WordCloudView_minimumTextSize, convertPixelToDIP(DEFAULT_MINIMUM_TEXT_SIZE));
            this.circleRadius = (int) a.getDimension(R.styleable.WordCloudView_circleRadius, 0) < 0 ? 0 : circleRadius;
            this.loadInit = a.getBoolean(R.styleable.WordCloudView_loadInit, false);
            this.loadingShowWords = a.getBoolean(R.styleable.WordCloudView_loadingShowWords, false);

            int wordPaddingRect = (int) a.getDimension(R.styleable.WordCloudView_wordPadding, 0);
            this.rectPadding = new Rect(wordPaddingRect, -1 * wordPaddingRect, wordPaddingRect, wordPaddingRect);       /* rectPadding is organized with TextPaint.getTextBounds() orientation (left, -1 * top, right, bottom) */

            int joinersID = a.getResourceId(R.styleable.WordCloudView_joiners, 0);          /*Joiners are characters that join words together i.e: ",", ";", ... */
            if (joinersID == 0) {
                joiners = new String[0];
            } else {
                joiners = context.getResources().getStringArray(R.array.english_joiners);
            }

            int unwantedWordsID = a.getResourceId(R.styleable.WordCloudView_unwanted_words, 0);
            if (unwantedWordsID == 0) {
                unwantedWords = new String[0];
            } else {
                unwantedWords = context.getResources().getStringArray(unwantedWordsID);
            }

            String expression = a.getString(R.styleable.WordCloudView_textSizeExpression);
            if (expression == null) {
                textSizeExpression = new ExpressionBuilder(REPETITION_VARIABLE + " + " +  DEFAULT_MINIMUM_TEXT_SIZE)
                        .variables(REPETITION_VARIABLE)
                        .build();
            } else {
                textSizeExpression = new ExpressionBuilder(expression)
                        .variables(REPETITION_VARIABLE)
                        .build();
            }

            fontPath = a.getResourceId(R.styleable.WordCloudView_wordsFont, 0);
            wordCloudColorsPath = a.getResourceId(R.styleable.WordCloudView_colors, 0);
        } finally {
            initPaint(fontPath, wordCloudColorsPath);
            processWords();
            a.recycle();
        }
    }

    private void initPaint(int fontPath, int wordCloudColorsPath) {
        textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(minimumTextSize);

        if (fontPath != 0) {
            textPaint.setTypeface(ResourcesCompat.getFont(context, fontPath));
        }

        if (wordCloudColorsPath != 0) {
            int[] colors = context.getResources().getIntArray(R.array.wordCloudExampleColors);

            for (int color : colors) {
                highestRepToLowestRepColors.add(color);
            }
        }
    }

    /*
    *   process words breaks down inputted words into a HashMap<Integer repetition, ArrayList<String> words>
    */
    public void processWords() {
        repToWords.clear();
        repToTextSize.clear();
        repAvailable.clear();
        wordToOpacity.clear();

        if (words != null && words.length() != 0) {
            removeJoiners();    /* remove joiners first to later on find unwanted words. i.e unwantedWord. -> unwantedWord || unwantedWord, -> unwantedWord */

            ArrayList<String> separatedWords = new ArrayList<>(Arrays.asList(words.split(" ")));

            removeUnwantedWords(separatedWords);

            for (String word : separatedWords) {
                boolean newWord = true;
                int rep;

                for (int i = 0; i < repAvailable.size(); i++) {
                    rep = repAvailable.get(i);
                    ArrayList<String> words = repToWords.get(rep);

                    if (words.contains(word)) {
                        words.remove(word);
                        if (words.size() == 0) {
                            repAvailable.remove(i);
                        }
                        rep++;
                        words = repToWords.get(rep);
                        if (words == null) {
                            words = new ArrayList<>();
                            repToWords.put(rep, words);
                        }

                        if (words.size() == 0) {
                            if (repAvailable.size() == 0) {
                                repAvailable.add(rep);
                            } else {
                                for (int r = 0; r < repAvailable.size(); r++) {
                                    if (rep < repAvailable.get(r)) {
                                        repAvailable.add(r, rep);
                                        break;
                                    } else if (r == repAvailable.size() - 1) {
                                        repAvailable.add(rep);
                                        break;
                                    }
                                }
                            }

                            words.add(word);
                            newWord = false;
                            break;
                        } else {
                            textPaint.setTextSize(minimumTextSize);
                            textPaint.getTextBounds(word, 0, word.length(), rectWord);

                            int area = (rectWord.left + rectWord.right) * (rectWord.bottom - rectWord.top);
                            for (int j = 0; j < words.size(); j++) {
                                String w = words.get(j);
                                textPaint.getTextBounds(w, 0, w.length(), rectWord);

                                int currentArea = (rectWord.left + rectWord.right) * (rectWord.bottom - rectWord.top);
                                if (currentArea < area) {
                                    words.add(j, word);
                                    break;
                                } else if(j == words.size() - 1) {
                                    words.add(word);
                                    break;
                                }
                            }
                        }

                        newWord = false;
                        break;
                    }
                }

                if (newWord) {
                    rep = 1;
                    if (!repAvailable.contains(1)) {
                        repAvailable.add(0, rep);
                    }

                    ArrayList<String> repOneWords = repToWords.get(1);
                    if (repOneWords == null) {
                        repOneWords = new ArrayList<>();
                        repOneWords.add(word);
                        repToWords.put(rep, repOneWords);
                    } else if (repOneWords.size() == 0) {
                        repOneWords.add(word);
                    } else {
                        textPaint.setTextSize(minimumTextSize);
                        textPaint.getTextBounds(word, 0, word.length(), rectWord);

                        int area = (rectWord.left + rectWord.right) * (rectWord.bottom - rectWord.top);
                        for (int j = 0; j < repOneWords.size(); j++) {
                            String w = repOneWords.get(j);
                            textPaint.getTextBounds(w, 0, w.length(), rectWord);

                            int currentArea = (rectWord.left + rectWord.right) * (rectWord.bottom - rectWord.top);
                            if (currentArea > area) {
                                repOneWords.add(j, word);
                                break;
                            } else if (j == repOneWords.size() - 1) {
                                repOneWords.add(word);
                                break;
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < repAvailable.size(); i++) {
                int rep = repAvailable.get(i);

                repToTextSize.put(rep, calculateTextSizeForRep(rep));
            }
        }
    }

    private void removeJoiners() {
        for (String joiner : joiners) {
            words = words.replace(joiner, "");
        }
    }

    private void removeUnwantedWords(ArrayList<String> separatedWords){
        Iterator<String> iterator = separatedWords.iterator();
        while (iterator.hasNext()) {
            String word = iterator.next();

            for (String unwantedWord : unwantedWords) {
                if (word.toLowerCase().equals(unwantedWord.toLowerCase())) {
                    iterator.remove();
                }
            }
        }
    }

    private float calculateTextSizeForRep(int rep) {
        try {
            float textSize = (float) textSizeExpression.setVariable(REPETITION_VARIABLE, rep).evaluate();

            textSize = textSize <= (minimumTextSize / dipToPixelScale) ?  minimumTextSize : convertPixelToDIP(textSize);
            return textSize;
        } catch (Exception e) {
            textSizeExpression = new ExpressionBuilder("0")
                    .build();

            return minimumTextSize;
        }
    }

    private float convertPixelToDIP(double pixel) {
        return (float) pixel * dipToPixelScale;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;
        collision = new boolean[height + 1][width + 1];
        if (Build.VERSION.SDK_INT >= 17) {
            if (getPaddingLeft() != 0) {
                paddingLeft = getPaddingLeft() > 0 ? getPaddingLeft() : 0;
            } else {
                paddingLeft = getPaddingStart() > 0 ? getPaddingStart() : 0;
            }

            if (getPaddingEnd() != 0) {
                paddingRight = getPaddingEnd() > 0 ? getPaddingEnd() : 0;
            } else {
                paddingRight = getPaddingRight() > 0 ? getPaddingRight() : 0;
            }

            if (getPaddingEnd() != 0) {
                paddingLeft = getPaddingEnd() < 0 ? paddingLeft - getPaddingEnd() : paddingLeft;
            } else {
                paddingLeft = getPaddingRight() < 0 ? paddingLeft - getPaddingRight() : paddingLeft;
            }

            if (getPaddingStart() != 0) {
                paddingRight = getPaddingStart() < 0 ? paddingRight - getPaddingStart() : paddingRight;
            } else {
                paddingRight = getPaddingLeft() < 0 ? paddingRight - getPaddingLeft() : paddingRight;
            }

            paddingTop = getPaddingTop() > 0 ? getPaddingTop() : 0;
            paddingBottom = getPaddingBottom() > 0 ? getPaddingBottom() : 0;

            paddingTop = getPaddingBottom() < 0 ? paddingTop - getPaddingBottom() : paddingTop;
            paddingBottom = getPaddingTop() < 0 ? paddingBottom - getPaddingTop() : paddingBottom;
        } else {
            paddingLeft = getPaddingLeft() > 0 ? getPaddingLeft() : 0;
            paddingTop = getPaddingTop() > 0 ? getPaddingTop() : 0;
            paddingRight = getPaddingRight() > 0 ? getPaddingRight() : 0;
            paddingBottom = getPaddingBottom() > 0 ? getPaddingBottom() : 0;

            paddingLeft = getPaddingRight() < 0 ? paddingLeft - getPaddingRight() : paddingLeft;
            paddingTop = getPaddingBottom() < 0 ? paddingTop - getPaddingBottom() : paddingTop;
            paddingRight = getPaddingLeft() < 0 ? paddingRight - getPaddingLeft() : paddingRight;
            paddingBottom = getPaddingTop() < 0 ? paddingBottom - getPaddingTop() : paddingBottom;
        }

        midpoint = new Point(paddingLeft + ((width - paddingLeft - paddingRight) / 2), paddingTop + ((height - paddingTop - paddingBottom) / 2));

        /*
        *    Desired loadWordPlacements: orientation change, start up
        *    Non-Desired loadWordPlacements for current app: keyboard is used
        */
        if (loadInit && !finishedLoadingInit) {
            loadWordPlacements();
        }
    }

    private void loadWordPlacements() {
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                loading = true;
                locateWordPlacements();
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        if (reset) {
                            resetLoadWordPlacements();
                        } else {
                            loading = false;

                            invalidate();
                            requestLayout();
                        }
                    }

                    /*
                    *    onError occurs when application is restarted
                    *    and loadWordPlacements() is called
                    *    before onSizeChanged() leaving midpoint = null causing a nullpointerexception.
                    */
                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof NullPointerException) {
                            loadWordPlacements();
                        }
                    }
                });
    }

    private void locateWordPlacements() {
        int distanceFromCenterX = (midpoint.x - paddingLeft);
        int distanceFromCenterY = (midpoint.y - paddingBottom);
        int spiralRadiiLimit = distanceFromCenterX > distanceFromCenterY ? distanceFromCenterX / 4 : distanceFromCenterY / 4;

        if (words != null) {
            initCollision2DArrayAndQuadrantAreas();
            verticalWordsPoints.clear();
            horizontalWordsPoints.clear();

            /*
             * Going through all of our words from highest to lowest repetition because on average,
             * we can fit more words into the canvas if we place the larger ones in first;
             * higher rep = bigger text size, lower rep = smaller text size
             */
            for (int i = repAvailable.size() - 1; i >= 0; i--) {
                int skips = 0;
                int rep = repAvailable.get(i);
                final ArrayList<String> words = repToWords.get(rep);

                textPaint.setTextSize(repToTextSize.get(rep));
                for (int w = 0; w < words.size(); w++) {
                    String word = words.get(w);

                    textPaint.getTextBounds(word, 0, word.length(), rectWord);
                    updateRotatedWordMeasurements(false);

                    int quadrant = findAreaWithMostSpace((wordRight + wordLeft) * (Math.abs(wordTop) + wordBottom));
                    if (quadrant == QUADRANTS_FULL) {
                        w = words.size() - 1;
                    } else {
                        int[] displacements = findQuadrantDisplacement(quadrant);

                        int xSpiralOrigin;
                        int ySpiralOrigin;
                        int rangeX;
                        int rangeY;
                        int spiralRadii = 0;

                        if (circleRadius != 0) {
                            rangeX = circleRadius;
                            rangeY = circleRadius;
                        } else {
                            rangeX = midpoint.x;
                            rangeY = midpoint.y;
                        }

                        xSpiralOrigin = rand.nextInt(rangeX) + displacements[0];
                        ySpiralOrigin = rand.nextInt(rangeY) + displacements[1];

                        boolean inserted = false;

                        resetSpiral();          /* every word has its own collision spiral so the spiral needs to be reset for every word */

                        while (!inserted && (xSpiralOrigin + spiral.left != xMin || xSpiralOrigin + spiral.right != xMax || ySpiralOrigin + spiral.bottom != yMax || ySpiralOrigin + spiral.top != yMin)) {
                            inserted = insertWord(word, xSpiralOrigin, ySpiralOrigin, spiralRadii);
                            if (!inserted) {
                                if (xSpiralOrigin + spiral.left != xMin) {
                                    spiral.left--;
                                }

                                if (xSpiralOrigin + spiral.right != xMax) {
                                    spiral.right++;
                                }

                                if (ySpiralOrigin + spiral.top != yMin) {
                                    spiral.top--;
                                }

                                if (ySpiralOrigin + spiral.bottom != yMax) {
                                    spiral.bottom++;
                                }

                                spiralRadii++;
                            }

                            if (spiralRadii == spiralRadiiLimit) {
                                skips++;
                                if (skips >= allowedSkips) {
                                    w = words.size();
                                }
                                break;
                            }
                        }
                    }
                }

                if (loadingShowWords) {
                    Completable.fromAction(new Action() {
                        @Override
                        public void run() {
                            ValueAnimator opacityAnimator = new ValueAnimator();
                            opacityAnimator.setValues(opacityProperty);
                            opacityAnimator.setDuration(DEFAULT_ANIMATION_TIME);
                            opacityAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    for (String word : words) {
                                        int opacity = reset ? 0 : (int) animation.getAnimatedValue(PROPERTY_OPACITY);
                                        wordToOpacity.put(word, opacity);
                                        invalidate();
                                    }
                                }
                            });

                            animatorSet.play(opacityAnimator);
                            opacityAnimator.start();
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                }

                if (reset) {
                    i = 0;
                }

            }
        }

    }

    /*
    *    collision[height][width] initialized with all false spaces
    *    false = space is not occupied    true = space is occupied
    *    depending on the desired shape and padding there will pre-filled true spaces
    *    i.e: circles will have true collision spaces where there is not a circle
    *    | 1 1 1 1 1|
    *    | 1 1 0 1 1|
    *    | 1 0 0 0 1|
    *    | 1 0 0 0 1|
    *    | 1 1 0 1 1|
     */
    private void initCollision2DArrayAndQuadrantAreas() {
        if (circleRadius != 0) {
            circleRadius = findCircleRadius();

            for (int y = midpoint.y; y >= 0; y--) {
                for (int x = midpoint.x; x >= 0; x--) {
                    int displacementX = 2 * (midpoint.x - x);
                    int displacementY = 2 * (midpoint.y - y);

                    boolean isCollision = true;
                    if (Math.sqrt(Math.pow(midpoint.x - x, 2) + Math.pow(midpoint.y - y, 2)) <= circleRadius) {
                        isCollision = false;
                    }
                    collision[y][x] = isCollision;
                    collision[y + displacementY][x] = isCollision;
                    collision[y][x + displacementX] = isCollision;
                    collision[y + displacementY][x + displacementX] = isCollision;
                }

            }

            double area = Math.PI * Math.pow(circleRadius, 2);
            estimateQuadrantSpaceAvailable[QUADRANT_2] = area;
            estimateQuadrantSpaceAvailable[QUADRANT_3] = area;
            estimateQuadrantSpaceAvailable[QUADRANT_1] = area;
            estimateQuadrantSpaceAvailable[QUADRANT_4] = area;

            xMax = midpoint.x + circleRadius <= width - paddingRight ? midpoint.x + circleRadius : width - paddingRight;
            xMin = midpoint.x - circleRadius >= paddingLeft ? midpoint.x - circleRadius : paddingLeft;
            yMax = midpoint.y + circleRadius <= height - paddingBottom ? midpoint.y + circleRadius : height - paddingBottom;
            yMin = midpoint.y - circleRadius >= paddingTop ? midpoint.y - circleRadius : paddingTop;
        } else {
            collision = new boolean[height + 1][width + 1];

            estimateQuadrantSpaceAvailable[QUADRANT_2] = (midpoint.x - paddingLeft) * (midpoint.y - paddingTop);
            estimateQuadrantSpaceAvailable[QUADRANT_3] = (midpoint.x - paddingLeft) * (height - midpoint.y - paddingBottom);
            estimateQuadrantSpaceAvailable[QUADRANT_1] = (width - midpoint.x - paddingRight) * (midpoint.y - paddingTop);
            estimateQuadrantSpaceAvailable[QUADRANT_4] = (width - midpoint.x - paddingRight) * (height - midpoint.y - paddingBottom);

            xMax = width - paddingRight;
            xMin = paddingLeft;
            yMax = height - paddingBottom;
            yMin = paddingTop;
        }
    }

    private int findCircleRadius() {
        int r;
        int pseudoCircleRadius = circleRadius;

        if (midpoint.x - pseudoCircleRadius < paddingLeft) {
            r = midpoint.x - paddingLeft;
            pseudoCircleRadius = r;
        }

        if (midpoint.x + pseudoCircleRadius > width - paddingRight) {
            r = width - paddingRight - midpoint.x;
            pseudoCircleRadius = pseudoCircleRadius > r ? r : pseudoCircleRadius;
        }

        if (midpoint.y + pseudoCircleRadius > height - paddingBottom) {
            r = height - paddingBottom - midpoint.y;
            pseudoCircleRadius = pseudoCircleRadius > r ? r : pseudoCircleRadius;
        }

        if (midpoint.y - pseudoCircleRadius < paddingTop) {
            r = midpoint.y - paddingTop;
            pseudoCircleRadius = pseudoCircleRadius > r ? r : pseudoCircleRadius;
        }

        return pseudoCircleRadius;
    }

    private int[] findQuadrantDisplacement(int quadrant) {
        int[] displacements = new int[2];

        if (circleRadius == 0) {
            switch (quadrant) {
                case QUADRANT_1:
                    displacements[0] = midpoint.x;
                    displacements[1] = paddingTop;
                    break;
                case QUADRANT_2:
                    displacements[0] = paddingLeft;
                    displacements[1] = paddingTop;
                    break;
                case QUADRANT_3:
                    displacements[0] = paddingLeft;
                    displacements[1] = midpoint.y;
                    break;
                default:
                    displacements[0] = midpoint.x;
                    displacements[1] = midpoint.y;
                    break;
            }
        } else {
            switch (quadrant) {
                case QUADRANT_1:
                    displacements[0] = midpoint.x;
                    displacements[1] = midpoint.y - circleRadius;
                    break;
                case QUADRANT_2:
                    displacements[0] = midpoint.x - circleRadius;
                    displacements[1] = midpoint.y - circleRadius;
                    break;
                case QUADRANT_3:
                    displacements[0] = midpoint.x - circleRadius;
                    displacements[1] = midpoint.y;
                    break;
                default:
                    displacements[0] = midpoint.x;
                    displacements[1] = midpoint.y;
                    break;
            }
        }

        return displacements;
    }

    private void resetSpiral() {
        spiral.left = 0;
        spiral.right = 0;
        spiral.top = 0;
        spiral.bottom = 0;
    }

    private int findAreaWithMostSpace(double wordArea) {
        int quadrant = QUADRANT_1;
        double biggestArea = estimateQuadrantSpaceAvailable[QUADRANT_1];

        for (int q = QUADRANT_2; q <= QUADRANT_4; q++) {
            double currentArea = estimateQuadrantSpaceAvailable[q];

            quadrant = currentArea > biggestArea ? q : quadrant;
            biggestArea = currentArea > biggestArea ? currentArea : biggestArea;
        }

        if (wordArea > biggestArea) {
            quadrant = QUADRANTS_FULL;
        }

        return quadrant;
    }

    /*
     * Attempts to insert the word in designed rectangle/portion of spiral.
     * If fails to insert the word in all given locations, returns false
     */
    private boolean insertWord(String word, int xSpiralOrigin, int ySpiralOrigin, int spiralRadii) {
        boolean inserted = false;

        for (int side = SIDE_BOTTOM; side <= SIDE_LEFT; side++) {
            boolean isSpiralHorizontalMovement = side % 2 == 0;

            int[] spiralInformation = getSpiralInformation(side, xSpiralOrigin, ySpiralOrigin, spiralRadii);
            int spiralStart = spiralInformation[0];
            int spiralEnd = spiralInformation[1];
            int spiralConstant = spiralInformation[2];
            int spiralIncrement = spiralInformation[3];

            for (int spiralMove = spiralStart; spiralMove <= spiralEnd; spiralMove += spiralIncrement) {
                int spiralX;
                int spiralY;

                if (isSpiralHorizontalMovement) {
                    spiralX = spiralMove;
                    spiralY = spiralConstant;
                } else {
                    spiralX = spiralConstant;
                    spiralY = spiralEnd - (spiralMove - spiralStart);
                }

                updateRotatedWordMeasurements(isSpiralHorizontalMovement);

                int collisionColumnStart = spiralX - wordLeft;
                int collisionColumnEnd = spiralX + wordRight;
                int collisionRowStart = spiralY + wordBottom;
                int collisionRowEnd = spiralY + wordTop;

                if (collisionColumnStart >= xMin
                        && collisionColumnEnd <= xMax
                        && collisionRowEnd >= yMin
                        && collisionRowStart <= yMax) {

                    for (int collisionX = collisionColumnStart; collisionX <= collisionColumnEnd; collisionX++) {
                        for (int collisionY = collisionRowStart; collisionY >= collisionRowEnd; collisionY--) {

                            if (collision[collisionY][collisionX]) {
                                if (!isSpiralHorizontalMovement) {
                                    do {
                                        collisionY--;
                                    }
                                    while (collisionY >= collisionRowEnd && collision[collisionY][collisionX] && collisionY >= spiralStart);
                                    spiralMove = spiralMove + Math.abs(spiralY - collisionY) + wordBottom;
                                }
                                collisionX = collisionColumnEnd;
                                collisionY = collisionRowEnd;

                            } else if (collisionX == collisionColumnEnd && collisionY == collisionRowEnd) {
                                insertWordIntoCollisionArray(collisionColumnStart, collisionColumnEnd, collisionRowStart, collisionRowEnd);
                                insertedWordUpdateQuadrantArea(collisionColumnStart, collisionColumnEnd, collisionRowEnd, collisionRowStart);

                                if (isSpiralHorizontalMovement) {
                                    textPaint.setTextAlign(Paint.Align.LEFT);
                                    horizontalWordsPoints.put(word, new Point(spiralX, spiralY));
                                } else {
                                    int displacementX;
                                    int displacementY;

                                    displacementX = midpoint.y - spiralY;
                                    displacementY = spiralX - midpoint.x;
                                    verticalWordsPoints.put(word, new Point(midpoint.x + displacementX, midpoint.y + displacementY));
                                }

                                inserted = true;
                                spiralMove = spiralEnd;
                                side = SIDE_EXIT;
                            }
                        }
                    }
                }
            }
        }

        return inserted;
    }

    /*
     * When traveling in our "spiral", the spiral will increase in both directions horizontally and vertically
     * horizontally before: ** after: +**+ = ****
     * vertically before:       after: +    =    *
     *                    *            *         *
     *                    *            *         *
     *                                 +         *
    */
    private int[] getSpiralInformation(int side, int xSpiralOrigin, int ySpiralOrigin, int spiralRadii) {
        int[] spiralInformation = new int[4];

        switch (side) {
            case SIDE_BOTTOM:
                spiralInformation[0] = xSpiralOrigin + spiral.left;
                spiralInformation[1] = xSpiralOrigin + spiral.right;
                spiralInformation[2] = ySpiralOrigin + spiral.bottom;
                spiralInformation[3] = ySpiralOrigin + spiral.bottom == yMax && spiralRadii != spiral.bottom ? Math.abs(spiral.left) + spiral.right : 1;
                break;
            case SIDE_RIGHT:
                spiralInformation[0] = ySpiralOrigin + spiral.top;
                spiralInformation[1] = ySpiralOrigin + spiral.bottom;
                spiralInformation[2] = xSpiralOrigin + spiral.right;
                spiralInformation[3] = xSpiralOrigin + spiral.right == xMax && spiralRadii != spiral.right ? Math.abs(spiral.top) + spiral.bottom : 1;
                break;
            case SIDE_TOP:
                spiralInformation[0] = xSpiralOrigin + spiral.left;
                spiralInformation[1] = xSpiralOrigin + spiral.right;
                spiralInformation[2] = ySpiralOrigin + spiral.top;
                spiralInformation[3] = ySpiralOrigin + spiral.top == yMin && spiralRadii != Math.abs(spiral.top) ? Math.abs(spiral.left) + spiral.right : 1;
                break;
            default:
                spiralInformation[0] = ySpiralOrigin + spiral.top;
                spiralInformation[1] = ySpiralOrigin + spiral.bottom;
                spiralInformation[2] = xSpiralOrigin + spiral.left;
                spiralInformation[3] = xSpiralOrigin + spiral.left == xMin && spiralRadii != Math.abs(spiral.left) ? Math.abs(spiral.top) + spiral.bottom : 1;
                break;
        }

        return spiralInformation;
    }

    private void updateRotatedWordMeasurements(boolean isHorizontal) {
        if (isHorizontal) {
            wordLeft = rectWord.left + rectPadding.left;
            wordRight = rectWord.right + rectPadding.right;
            wordTop = rectWord.top + rectPadding.top;
            wordBottom = rectWord.bottom +  rectPadding.bottom;
        } else {
            wordLeft = Math.abs(rectWord.top + rectPadding.top);
            wordRight = rectWord.bottom + rectPadding.bottom;
            wordTop = -1 * (rectWord.right + rectPadding.right);
            wordBottom = rectWord.left +  rectPadding.left;
        }
    }

    private void insertWordIntoCollisionArray(int xStart, int xEnd, int yStart, int yEnd) {
        for (int row = yStart; row >= yEnd; row--) {
            for (int column = xStart; column < xEnd; column++) {
                collision[row][column] = true;
            }
        }
    }


    /*
     * words are not only inserted into one quadrant
     * words can be inserted into multiple quadrants
     * i.e placing a word in the center would make the word occupy all quadrants
     */
    private void insertedWordUpdateQuadrantArea(int wordLeft, int wordRight, int wordTop, int wordBottom) {
        int wordSegmentWidth;
        int wordSegmentHeight;
        int quadrant;
        int area;

        // word can be in in Q1 and Q4 or either
        if (wordRight > midpoint.x) {
            if (wordLeft > midpoint.x) {
                wordSegmentWidth = wordRight - wordLeft;
            } else {
                wordSegmentWidth = wordRight - midpoint.x;
            }

            if (wordBottom > midpoint.y) {
                //word's bottom is in Q4
                if (wordTop > midpoint.y) {
                    //word's top  is in Q4
                    wordSegmentHeight = wordBottom - wordTop;
                    quadrant = QUADRANT_4;
                } else {
                    //word's top is in Q1
                    wordSegmentHeight = midpoint.y - wordTop;
                    quadrant = QUADRANT_1;
                    area = wordSegmentWidth * wordSegmentHeight;
                    estimateQuadrantSpaceAvailable[quadrant] -= area;
                    wordSegmentHeight = wordBottom - midpoint.y;
                    quadrant = QUADRANT_4;
                }

                area = wordSegmentWidth * wordSegmentHeight;
                estimateQuadrantSpaceAvailable[quadrant] -= area;
            } else {
                // word's bottom is in Q1 therefore word's top is in Q1
                wordSegmentHeight = wordBottom - wordTop;
                area = wordSegmentWidth * wordSegmentHeight;
                quadrant = QUADRANT_1;
                estimateQuadrantSpaceAvailable[quadrant] -= area;
            }
        }

        if (wordLeft <= midpoint.x) {
            //word's left is in Q2 and Q3 or either
            if (wordRight <= midpoint.x) {
                wordSegmentWidth = wordRight - wordLeft;
            } else {
                wordSegmentWidth = midpoint.x - wordLeft;
            }

            if (wordBottom > midpoint.y) {
                if (wordTop > midpoint.y) {
                    //word's bottom is in Q3
                    wordSegmentHeight = wordBottom - wordTop;
                    quadrant = QUADRANT_3;
                } else {
                    //word's bottom is in Q2
                    wordSegmentHeight = midpoint.y - wordTop;
                    quadrant = QUADRANT_2;
                    area = wordSegmentWidth * wordSegmentHeight;
                    estimateQuadrantSpaceAvailable[quadrant] -= area;
                    wordSegmentHeight = wordBottom - midpoint.y;
                    quadrant = QUADRANT_3;
                }

                area = wordSegmentWidth * wordSegmentHeight;
                estimateQuadrantSpaceAvailable[quadrant] -= area;
            } else {
                //word's bottom in is Q2 meaning word's top is in Q2;
                wordSegmentHeight = wordBottom - wordTop;
                quadrant = QUADRANT_2;
                area = wordSegmentWidth * wordSegmentHeight;
                estimateQuadrantSpaceAvailable[quadrant] -= area;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ((!loadingShowWords && !loading) || (loadingShowWords)) {
            Iterator<String> horizontalIterator = horizontalWordsPoints.keySet().iterator();
            while (horizontalIterator.hasNext()) {
                String word = horizontalIterator.next();
                Point point = horizontalWordsPoints.get(word);

                setPaint(textPaint, word);

                canvas.drawText(word, point.x, point.y, textPaint);

            }
            canvas.rotate(-90, midpoint.x, midpoint.y);

            Iterator<String> verticalIterator = verticalWordsPoints.keySet().iterator();
            while (verticalIterator.hasNext()) {
                String word = verticalIterator.next();
                Point point = verticalWordsPoints.get(word);

                setPaint(textPaint, word);
                canvas.drawText(word, point.x, point.y, textPaint);
            }
            canvas.rotate(90, midpoint.x, midpoint.y);

            if (!loading) {
                if (presenter != null) {
                    presenter.wordCloudDoneLoading();
                    if (!finishedLoadingInit) {
                        finishedLoadingInit = true;
                    }
                }
            }
        }
    }

    private void setPaint(Paint paint, String word) {
        for (int i = 0; i < repAvailable.size(); i++) {
            int rep = repAvailable.get(i);

            if (repToWords.get(rep).contains(word)) {
                paint.setTextSize(repToTextSize.get(rep));
                if (paint == textPaint) {
                    int colorIndex = repAvailable.size() - 1 - i;

                    if (colorIndex < highestRepToLowestRepColors.size()) {
                        paint.setColor(highestRepToLowestRepColors.get(colorIndex));
                    } else {
                        paint.setColor(defaultColor);
                    }
                }

                if (loadingShowWords) {
                    paint.setAlpha(wordToOpacity.get(word) == null ? 0 : wordToOpacity.get(word));
                }

                i = repAvailable.size() - 1;
            }
        }
    }

    public void loadWordCloud() {
        if (loading) {
            reset = true;
        } else {
            resetLoadWordPlacements();
        }

    }

    public void resetLoadWordPlacements() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                stopAnimationSet();
                reset = false;
                loading = true;
                horizontalWordsPoints.clear();
                verticalWordsPoints.clear();
                invalidate();
                requestLayout();
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.computation())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        loadWordPlacements();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });


    }

    private void stopAnimationSet() {
        PropertyValuesHolder reverseOpacityProperty = PropertyValuesHolder.ofInt(PROPERTY_OPACITY, 0, 0);
        ArrayList<Animator> animators = animatorSet.getChildAnimations();

        for (Animator animator : animators) {
            ValueAnimator valueAnimator = (ValueAnimator) animator;
            valueAnimator.setValues(reverseOpacityProperty);
        }
        animatorSet.end();
        animatorSet = new AnimatorSet();
    }

    public void setPresenter(WordCloudContract.UserActionListener presenter) {
        this.presenter = presenter;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
        processWords();
    }

    public Expression getTextSizeExpression() {
        return textSizeExpression;
    }

    public void setTextSizeExpression(String textSizeExpression) {
        try {
            this.textSizeExpression = new ExpressionBuilder(textSizeExpression)
                    .variables(REPETITION_VARIABLE)
                    .build();
        } catch (Exception e) {
            this.textSizeExpression = new ExpressionBuilder("repetition" + WordCloudView.DEFAULT_MINIMUM_TEXT_SIZE)
                    .variables(REPETITION_VARIABLE)
                    .build();
        }
    }

    public int getAllowedSkips() {
        return allowedSkips;
    }

    public void setAllowedSkips(int allowedSkips) {
        this.allowedSkips = allowedSkips;
    }

    public boolean isLoadingShowWords() {
        return loadingShowWords;
    }

    public void setLoadingShowWords(boolean loadingShowWords) {
        this.loadingShowWords = loadingShowWords;
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
    }

    public Typeface getTypeface() {
        return textPaint.getTypeface();
    }

    public void setTypeface(int fontPath) {
        textPaint.setTypeface(ResourcesCompat.getFont(context, fontPath));
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

}
