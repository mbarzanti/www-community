/*
Pattern #2: Props as a dependency in useMemo
Let us do it correctly as well as optimally this time. It all started with Anti-Pattern #1: props or context as the initial state.

We can fix this by using props as a dependency in useMemo. By moving the total character count computation to useMemo hook in Optimal.tsx (line 22), we are able to prevent network request to fetch emoji unless the total character count has changed.
*/
import { useCallback, useEffect, useMemo, useState } from "react";
import { Navigation } from "../components/Navigation";
import { useGetArticles } from "../hooks/useGetArticles";
import { useGetEmoji } from "../hooks/useGetEmoji";
import { Articles } from "../types";

const styles: { [key: string]: React.CSSProperties } = {
  container: {
    background: "#F0FDF4",
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
  const length = useMemo<number>(
    () => props.article.text.length + props.article.title.length,
    [props]
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

const Optimal: React.FC = () => {
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
        {currentArticle ? <ArticleContent article={currentArticle} /> : null}
      </div>
    </div>
  );
};

export default Optimal;