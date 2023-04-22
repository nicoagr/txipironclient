package eus.ehu.txipironesmastodonfx.domain;

import java.util.List;

/**
 * This class represents the result of a search.
 * For the moment, it will include a list of accounts and a list of statuses.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class SearchResult {
    public List<Follow> accounts;
    public List<Toot> statuses;
}
