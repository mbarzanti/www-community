/*
Anti-Pattern #2: Destroy and Recreate
Let us make amends for our incorrect approach by using the ‘Destroy and Recreate’ anti-pattern.

Destroying a functional component refers to destroying all the hooks and the states created during the first function call. Recreating refers to calling the function again as if it had been never called before.

Note that a parent component can use the key prop to destroy the component and recreate it every time the key changes. Yes, you read it right — you can use keys outside loops.

Specifically, we implement ‘Destroy and Recreate’ anti-pattern by using the key prop while rendering the child component ArticleContent of the parent component PartiallyCorrect in the PartiallyCorrect.tsx file (line 65).
*/
import { useCallback, useEffect, useState } from "react";
import { Navigation } from "../components/Navigation";
import { useGetArticles } from "../hooks/useGetArticles";
import { useGetEmoji } from "../hooks/useGetEmoji";
import { Articles } from "../types";

const styles: { [key: string]: React.CSSProperties } = {
  container: {
    background: "#FEF2F2",
    height: "100%",
    display: "grid",
    gridTemplateColumns: "10rem auto"
  },
  content: {}
};

const ArticleContent: React.FC<{
  article: Articles["articles"]["0"];
}> = (props) => {
  // Step 1. calculate length as we need it to get corresponding emotion
  const [length] = useState<number>(
    props.article.text.length + props.article.title.length
  );

  // Step 2. fetch emotion map from backend
  const emotions = useGetEmoji();

  // Step 3. set emotion once we get emotion map from backend
  const [emotion, setEmotion] = useState<string>("");
  useEffect(() => {
    if (emotions) {
      setEmotion(emotions["stickers"][length]);
    }
  }, [emotions, length]);

  return (
    <div>
      <div>
        <h2>{props.article.title}</h2>
        <div>{props.article.text}</div>
      </div>
      <h3
        dangerouslySetInnerHTML={{
          __html: `Total Length ${length} ${emotion}`
        }}
      />
    </div>
  );
};

const PartiallyCorrect: React.FC = () => {
  const articles = useGetArticles();
  const [currentArticle, setCurrentArticle] = useState<
    Articles["articles"]["0"] | null
  >();
  const onClickHandler = useCallback((article) => {
    setCurrentArticle(article);
  }, []);
  return (
    <div style={styles.container}>
      <Navigation articles={articles} onClickHandler={onClickHandler} />
      <div style={styles.content}>
        {/** Step 4. Using key to force destroy and recreate */}
        {currentArticle ? (
          <ArticleContent article={currentArticle} key={currentArticle.id} />
        ) : null}
      </div>
    </div>
  );
};

export default PartiallyCorrect;