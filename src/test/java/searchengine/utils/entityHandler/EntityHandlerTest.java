// package searchengine.utils.entityHandler;
//
// public class EntityHandlerTest {

  //
  //    @Test
  //    public void testGetIndexedSiteModelFromSites_MultipleSites() {
  //        Site site1 = new Site("https://example.com");
  //        SiteModel siteModel1 = new SiteModel(site1.getUrl());
  //
  //        Site site2 = new Site("https://example.org");
  //        SiteModel siteModel2 = new SiteModel(site2.getUrl());
  //
  //        List<Site> sitesToParse = new ArrayList<>();
  //        sitesToParse.add(site1);
  //        sitesToParse.add(site2);
  //
  //        List<SiteModel> expected = new ArrayList<>();
  //        expected.add(siteModel1);
  //        expected.add(siteModel2);
  //
  //        when(siteRepository.findSiteByUrl(site1.getUrl())).thenReturn(Optional.empty());
  //        when(entityFactory.createSiteModel(site1)).thenReturn(siteModel1);
  //
  //        when(siteRepository.findSiteByUrl(site2.getUrl())).thenReturn(Optional.empty());
  //        when(entityFactory.createSiteModel(site2)).thenReturn(siteModel2);
  //
  //        List<SiteModel> result = entityHandler.getIndexedSiteModelFromSites(sitesToParse);
  //
  //        assertEquals(expected, result);
  //    }
// }
