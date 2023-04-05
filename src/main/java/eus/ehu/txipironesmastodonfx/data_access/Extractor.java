//
// Source code recreated from a .class file by IntelliJ IDEA
//

package eus.ehu.txipironesmastodonfx.data_access;

import java.util.*;
import java.util.regex.Matcher;

/**
 * Class used to Parse Toot's HTML
 * Extracted from the com.twitter package
 *
 * @author Twitter, Inc.
 */
public class Extractor {
    protected boolean extractURLWithoutProtocol = true;

    public Extractor() {
    }

    private void removeOverlappingEntities(List<Entity> entities) {
        Collections.sort(entities, new Comparator<Entity>() {
            public int compare(Entity e1, Entity e2) {
                return e1.start - e2.start;
            }
        });
        if (!entities.isEmpty()) {
            Iterator<Entity> it = entities.iterator();
            Entity prev = (Entity) it.next();

            while (it.hasNext()) {
                Entity cur = (Entity) it.next();
                if (prev.getEnd() > cur.getStart()) {
                    it.remove();
                } else {
                    prev = cur;
                }
            }
        }

    }

    public List<Entity> extractEntitiesWithIndices(String text) {
        List<Entity> entities = new ArrayList();
        entities.addAll(this.extractURLsWithIndices(text));
        entities.addAll(this.extractHashtagsWithIndices(text, false));
        entities.addAll(this.extractMentionsOrListsWithIndices(text));
        entities.addAll(this.extractCashtagsWithIndices(text));
        this.removeOverlappingEntities(entities);
        return entities;
    }

    public List<String> extractMentionedScreennames(String text) {
        if (text != null && !text.isEmpty()) {
            List<String> extracted = new ArrayList();
            Iterator i$ = this.extractMentionedScreennamesWithIndices(text).iterator();

            while (i$.hasNext()) {
                Entity entity = (Entity) i$.next();
                extracted.add(entity.value);
            }

            return extracted;
        } else {
            return Collections.emptyList();
        }
    }

    public List<Entity> extractMentionedScreennamesWithIndices(String text) {
        List<Entity> extracted = new ArrayList();
        Iterator i$ = this.extractMentionsOrListsWithIndices(text).iterator();

        while (i$.hasNext()) {
            Entity entity = (Entity) i$.next();
            if (entity.listSlug == null) {
                extracted.add(entity);
            }
        }

        return extracted;
    }

    public List<Entity> extractMentionsOrListsWithIndices(String text) {
        if (text != null && !text.isEmpty()) {
            boolean found = false;
            char[] arr$ = text.toCharArray();
            int len$ = arr$.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                char c = arr$[i$];
                if (c == '@' || c == '＠') {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return Collections.emptyList();
            } else {
                List<Entity> extracted = new ArrayList();
                Matcher matcher = Regex.VALID_MENTION_OR_LIST.matcher(text);

                while (matcher.find()) {
                    String after = text.substring(matcher.end());
                    if (!Regex.INVALID_MENTION_MATCH_END.matcher(after).find()) {
                        if (matcher.group(4) == null) {
                            extracted.add(new Entity(matcher, Extractor.Entity.Type.MENTION, 3));
                        } else {
                            extracted.add(new Entity(matcher.start(3) - 1, matcher.end(4), matcher.group(3), matcher.group(4), Extractor.Entity.Type.MENTION));
                        }
                    }
                }

                return extracted;
            }
        } else {
            return Collections.emptyList();
        }
    }

    public String extractReplyScreenname(String text) {
        if (text == null) {
            return null;
        } else {
            Matcher matcher = Regex.VALID_REPLY.matcher(text);
            if (matcher.find()) {
                String after = text.substring(matcher.end());
                return Regex.INVALID_MENTION_MATCH_END.matcher(after).find() ? null : matcher.group(1);
            } else {
                return null;
            }
        }
    }

    public List<String> extractURLs(String text) {
        if (text != null && !text.isEmpty()) {
            List<String> urls = new ArrayList();
            Iterator i$ = this.extractURLsWithIndices(text).iterator();

            while (i$.hasNext()) {
                Entity entity = (Entity) i$.next();
                urls.add(entity.value);
            }

            return urls;
        } else {
            return Collections.emptyList();
        }
    }

    public List<Entity> extractURLsWithIndices(String text) {
        if (text != null && !text.isEmpty() && (this.extractURLWithoutProtocol ? text.indexOf(46) : text.indexOf(58)) != -1) {
            List<Entity> urls = new ArrayList();
            Matcher matcher = Regex.VALID_URL.matcher(text);

            while (true) {
                do {
                    if (!matcher.find()) {
                        return urls;
                    }
                } while (matcher.group(4) == null && (!this.extractURLWithoutProtocol || Regex.INVALID_URL_WITHOUT_PROTOCOL_MATCH_BEGIN.matcher(matcher.group(2)).matches()));

                String url = matcher.group(3);
                int start = matcher.start(3);
                int end = matcher.end(3);
                Matcher tco_matcher = Regex.VALID_TCO_URL.matcher(url);
                if (tco_matcher.find()) {
                    url = tco_matcher.group();
                    end = start + url.length();
                }

                urls.add(new Entity(start, end, url, Extractor.Entity.Type.URL));
            }
        } else {
            return Collections.emptyList();
        }
    }

    public List<String> extractHashtags(String text) {
        if (text != null && !text.isEmpty()) {
            List<String> extracted = new ArrayList();
            Iterator i$ = this.extractHashtagsWithIndices(text).iterator();

            while (i$.hasNext()) {
                Entity entity = (Entity) i$.next();
                extracted.add(entity.value);
            }

            return extracted;
        } else {
            return Collections.emptyList();
        }
    }

    public List<Entity> extractHashtagsWithIndices(String text) {
        return this.extractHashtagsWithIndices(text, true);
    }

    private List<Entity> extractHashtagsWithIndices(String text, boolean checkUrlOverlap) {
        if (text != null && !text.isEmpty()) {
            boolean found = false;
            char[] arr$ = text.toCharArray();
            int len$ = arr$.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                char c = arr$[i$];
                if (c == '#' || c == '＃') {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return Collections.emptyList();
            } else {
                List<Entity> extracted = new ArrayList();
                Matcher matcher = Regex.VALID_HASHTAG.matcher(text);

                while (matcher.find()) {
                    String after = text.substring(matcher.end());
                    if (!Regex.INVALID_HASHTAG_MATCH_END.matcher(after).find()) {
                        extracted.add(new Entity(matcher, Extractor.Entity.Type.HASHTAG, 3));
                    }
                }

                if (checkUrlOverlap) {
                    List<Entity> urls = this.extractURLsWithIndices(text);
                    if (!urls.isEmpty()) {
                        extracted.addAll(urls);
                        this.removeOverlappingEntities(extracted);
                        Iterator<Entity> it = extracted.iterator();

                        while (it.hasNext()) {
                            Entity entity = (Entity) it.next();
                            if (entity.getType() != Extractor.Entity.Type.HASHTAG) {
                                it.remove();
                            }
                        }
                    }
                }

                return extracted;
            }
        } else {
            return Collections.emptyList();
        }
    }

    public List<String> extractCashtags(String text) {
        if (text != null && !text.isEmpty()) {
            List<String> extracted = new ArrayList();
            Iterator i$ = this.extractCashtagsWithIndices(text).iterator();

            while (i$.hasNext()) {
                Entity entity = (Entity) i$.next();
                extracted.add(entity.value);
            }

            return extracted;
        } else {
            return Collections.emptyList();
        }
    }

    public List<Entity> extractCashtagsWithIndices(String text) {
        if (text != null && !text.isEmpty()) {
            if (text.indexOf(36) == -1) {
                return Collections.emptyList();
            } else {
                List<Entity> extracted = new ArrayList();
                Matcher matcher = Regex.VALID_CASHTAG.matcher(text);

                while (matcher.find()) {
                    extracted.add(new Entity(matcher, Extractor.Entity.Type.CASHTAG, 3));
                }

                return extracted;
            }
        } else {
            return Collections.emptyList();
        }
    }

    public void setExtractURLWithoutProtocol(boolean extractURLWithoutProtocol) {
        this.extractURLWithoutProtocol = extractURLWithoutProtocol;
    }

    public boolean isExtractURLWithoutProtocol() {
        return this.extractURLWithoutProtocol;
    }

    public void modifyIndicesFromUnicodeToUTF16(String text, List<Entity> entities) {
        IndexConverter convert = new IndexConverter(text);

        Entity entity;
        for (Iterator i$ = entities.iterator(); i$.hasNext(); entity.end = convert.codePointsToCodeUnits(entity.end)) {
            entity = (Entity) i$.next();
            entity.start = convert.codePointsToCodeUnits(entity.start);
        }

    }

    public void modifyIndicesFromUTF16ToToUnicode(String text, List<Entity> entities) {
        IndexConverter convert = new IndexConverter(text);

        Entity entity;
        for (Iterator i$ = entities.iterator(); i$.hasNext(); entity.end = convert.codeUnitsToCodePoints(entity.end)) {
            entity = (Entity) i$.next();
            entity.start = convert.codeUnitsToCodePoints(entity.start);
        }

    }

    private static final class IndexConverter {
        protected final String text;
        protected int codePointIndex = 0;
        protected int charIndex = 0;

        IndexConverter(String text) {
            this.text = text;
        }

        int codeUnitsToCodePoints(int charIndex) {
            if (charIndex < this.charIndex) {
                this.codePointIndex -= this.text.codePointCount(charIndex, this.charIndex);
            } else {
                this.codePointIndex += this.text.codePointCount(this.charIndex, charIndex);
            }

            this.charIndex = charIndex;
            if (charIndex > 0 && Character.isSupplementaryCodePoint(this.text.codePointAt(charIndex - 1))) {
                --this.charIndex;
            }

            return this.codePointIndex;
        }

        int codePointsToCodeUnits(int codePointIndex) {
            this.charIndex = this.text.offsetByCodePoints(this.charIndex, codePointIndex - this.codePointIndex);
            this.codePointIndex = codePointIndex;
            return this.charIndex;
        }
    }

    public static class Entity {
        protected int start;
        protected int end;
        protected final String value;
        protected final String listSlug;
        protected final Type type;
        protected String displayURL;
        protected String expandedURL;

        public Entity(int start, int end, String value, String listSlug, Type type) {
            this.displayURL = null;
            this.expandedURL = null;
            this.start = start;
            this.end = end;
            this.value = value;
            this.listSlug = listSlug;
            this.type = type;
        }

        public Entity(int start, int end, String value, Type type) {
            this(start, end, value, (String) null, type);
        }

        public Entity(Matcher matcher, Type type, int groupNumber) {
            this(matcher, type, groupNumber, -1);
        }

        public Entity(Matcher matcher, Type type, int groupNumber, int startOffset) {
            this(matcher.start(groupNumber) + startOffset, matcher.end(groupNumber), matcher.group(groupNumber), type);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (!(obj instanceof Entity)) {
                return false;
            } else {
                Entity other = (Entity) obj;
                return this.type.equals(other.type) && this.start == other.start && this.end == other.end && this.value.equals(other.value);
            }
        }

        public int hashCode() {
            return this.type.hashCode() + this.value.hashCode() + this.start + this.end;
        }

        public String toString() {
            return this.value + "(" + this.type + ") [" + this.start + "," + this.end + "]";
        }

        public Integer getStart() {
            return this.start;
        }

        public Integer getEnd() {
            return this.end;
        }

        public String getValue() {
            return this.value;
        }

        public String getListSlug() {
            return this.listSlug;
        }

        public Type getType() {
            return this.type;
        }

        public String getDisplayURL() {
            return this.displayURL;
        }

        public void setDisplayURL(String displayURL) {
            this.displayURL = displayURL;
        }

        public String getExpandedURL() {
            return this.expandedURL;
        }

        public void setExpandedURL(String expandedURL) {
            this.expandedURL = expandedURL;
        }

        public static enum Type {
            URL,
            HASHTAG,
            MENTION,
            CASHTAG;

            private Type() {
            }
        }
    }
}
