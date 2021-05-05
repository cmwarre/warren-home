package co.warrenhome.tags;

import com.inductiveautomation.ignition.common.browsing.BrowseFilter;
import com.inductiveautomation.ignition.common.browsing.Results;
import com.inductiveautomation.ignition.common.config.BoundPropertySet;
import com.inductiveautomation.ignition.common.model.values.QualityCode;
import com.inductiveautomation.ignition.common.sqltags.model.TagProviderMeta;
import com.inductiveautomation.ignition.common.sqltags.model.types.DataType;
import com.inductiveautomation.ignition.common.tags.browsing.NodeDescription;
import com.inductiveautomation.ignition.common.tags.paths.parser.TagPathParser;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.tags.managed.DeletionHandler;
import com.inductiveautomation.ignition.gateway.tags.managed.ManagedTagProvider;
import com.inductiveautomation.ignition.gateway.tags.managed.ProviderConfiguration;
import com.inductiveautomation.ignition.gateway.tags.managed.WriteHandler;
import lombok.Synchronized;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Managed Tag Provider exposed as a singleton object for easy access in the rest of the project
 * */
public class ISYTagProvider implements ManagedTagProvider {

    private static final LoggerEx logger = LoggerEx.newBuilder().build(ISYTagProvider.class);
    private ManagedTagProvider provider;
    private final GatewayContext gatewayContext;

    public ISYTagProvider(GatewayContext gatewayContext) {
        this.gatewayContext = gatewayContext;
        startup();
    }

    /**
     * Startup MES Tag Provider
     * */
    public void startup(){

        ProviderConfiguration configuration = new ProviderConfiguration("ISY")
                .setAllowTagCustomization(true) // allow users to override and configure history/documentation on tags
                .setAllowTagDeletion(true) // allow users to delete tags
                .setPersistTags(true) // keep memory of tag structure on restart
                .setPersistValues(false) // force re-load of tag values on startup
                .setAttribute(TagProviderMeta.FLAG_HAS_OPCBROWSE, false); // no OPC browsing

        setProvider(gatewayContext.getTagManager().getOrCreateManagedProvider(configuration));
    }

    /**
     * Gracefully shutdown tag provider
     * */
    public void shutdown() {
        try {
            shutdown(true);
        }catch (Exception e){
            logger.error("Uncaught exception when shutting down MES Tag Provider", e);
        }
        provider = null;
    }

    @Synchronized
    private void setProvider(ManagedTagProvider provider){
        this.provider = provider;
    }

    /**
     * @return MESTagProvider Provider Name
     * */
    public String getProviderName(){
        return "ISY";
    }

    /**
     * Directly Browses this Tag Provider asynchronously
     * */
    public CompletableFuture<Results<NodeDescription>> browseAsync(String tagPath, BrowseFilter filter){
        return gatewayContext.getTagManager().browseAsync(TagPathParser.parseSafe(getProviderName(), tagPath), filter);
    }

    /**
     * Directly Browses this Tag Provider synchronously and gracefully handles exceptions
     * @param tagPath root path to browse from
     * */
    public Results<BrowseTag> browse(String tagPath, BrowseFilter filter){
        try {
            return transformToBrowseTag(browseAsync(tagPath, filter).get(), tagPath);
        } catch (InterruptedException | ExecutionException e) {
            logger.warn("Exception thrown while browsing tags", e);
        }
        return null;
    }

    /**
     * Recursively browse the tag provider
     * @param tagPath root path to browse from
     * @param filter Ignition BrowseFilter object
     * */
    public Results<BrowseTag> browseRecursive(String tagPath, BrowseFilter filter){
        return browseRecursiveImpl(browse(tagPath, filter), tagPath, filter);
    }

    /**
     * Implicit declaration of the recursive browse function.  This is a recursive function, so needs to be private
     * and separated from the main
     * */
    private Results<BrowseTag> browseRecursiveImpl(Results<BrowseTag> results, String tagPath, BrowseFilter filter) {
        // make an immutable collection to prevent a ConcurrentModificationException due to iterating and adding results
        final ArrayList<BrowseTag> _results = new ArrayList<>(results.getResults());

        for(BrowseTag node : _results){
            if(node.hasChildren()){
                String newPath = String.format("%s/%s", tagPath, node.getName());
                Results<BrowseTag> newResults = browseRecursiveImpl(browse(newPath, filter), newPath, filter);
                results.getResults().addAll(newResults.getResults());
            }
        }

        return results;
    }

    /**
     * Transforms Ignition's Results<NodeDescription> object to a browse tag object that will actually
     * provide us with tag paths...
     * */
    private Results<BrowseTag> transformToBrowseTag(final Results<NodeDescription> results, final String tagPath){
        final Results<BrowseTag> newResults = new Results<>();
        newResults.setResults(new ArrayList<>());
        results.getResults().forEach(r ->
                newResults.getResults().add(new BrowseTag(r, String.format("%s/%s", tagPath, r.getName()))));
        return newResults;
    }


    /*
    * The following methods are exposing the managed tag provider methods through the singleton instance.
    *
    * */
    // <editor-fold desc="Managed Tag Provider Methods">

    @Override
    public void updateValue(String path, Object value, QualityCode quality) {
        provider.updateValue(path, value, quality);
    }

    @Override
    public void updateValue(String s, Object o, QualityCode qualityCode, Date date) {
        provider.updateValue(s, o, qualityCode, date);
    }

    @Override
    public void configureTag(String s, BoundPropertySet boundPropertySet) {
        provider.configureTag(s, boundPropertySet);
    }

    @Override
    public void configureTag(String path, DataType dataType) {
        provider.configureTag(path, dataType);
    }

    @Override
    public void removeTag(String s) {
        provider.removeTag(s);
    }

    @Override
    public void registerWriteHandler(String s, WriteHandler writeHandler) {
        provider.registerWriteHandler(s, writeHandler);
    }

    @Override
    public void setDeletionHandler(DeletionHandler deletionHandler) {
        provider.setDeletionHandler(deletionHandler);
    }

    @Override
    public void shutdown(boolean b) {
        provider.shutdown(b);
    }

    // </editor-fold>

}
