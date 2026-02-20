#!/bin/sh

VERSION=$(jq .version ./package.json)
SPLIT_VERSION=(${VERSION//./ })

MAJOR=${SPLIT_VERSION[0]//\"/}
MIDDLE=${SPLIT_VERSION[1]//\"/}
MINOR=${SPLIT_VERSION[2]//\"/}

echo "Which build you need?"
echo "  [1] Major"
echo "  [2] Middle"
echo "  [3] Minor"

while :
do
    read RELEASE_TYPE
    case $RELEASE_TYPE in
        1)
            MAJOR=$((MAJOR+1))
            MIDDLE=0
            MINOR=0
            NEW_VERSION="\"$MAJOR.$MIDDLE.$MINOR\""
            break
            ;;
        2)
            MIDDLE=$((MIDDLE+1))
            MINOR=0
            NEW_VERSION="\"$MAJOR.$MIDDLE.$MINOR\""
            break
            ;;
        3)
            MINOR=$((MINOR+1))
            NEW_VERSION="\"$MAJOR.$MIDDLE.$MINOR\""
            break
            ;;
        *)
		    echo "Ops... there is an error. Retry!"
		    ;;
    esac
done
echo
echo $NEW_VERSION

cp ./package.json ./package-bkp.json

rm -rf ./package.json

FILE=$(jq . ./package-bkp.json)

NEW_FILE=${FILE//$VERSION/$NEW_VERSION}

echo $NEW_FILE > package.json

rm -rf ./package-bkp.json

NEW_VERSION_CLEANED=${NEW_VERSION//\"/}

# git commit package json
git add .
git commit -am "increment version"
git checkout -b release/$NEW_VERSION_CLEANED
git push gitlab release/$NEW_VERSION_CLEANED

# git create tag
git tag $NEW_VERSION_CLEANED
git push gitlab --tags
