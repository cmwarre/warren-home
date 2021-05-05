package co.warrenhome.tags;

import com.inductiveautomation.gateway.tags.history.actor.TagHistoryProps;
import com.inductiveautomation.ignition.common.config.BasicBoundPropertySet;
import com.inductiveautomation.ignition.common.sqltags.history.InterpolationMode;
import com.inductiveautomation.ignition.common.sqltags.model.types.DataType;
import com.inductiveautomation.ignition.common.sqltags.model.types.DeadbandMode;
import com.inductiveautomation.ignition.common.tags.config.SampleMode;
import com.inductiveautomation.ignition.common.util.TimeUnits;
import com.inductiveautomation.ignition.gateway.sqltags.config.TagHistoryProviderRecord;
import com.inductiveautomation.ignition.common.tags.config.properties.WellKnownTagProps;

import java.util.Objects;

public class TagConfig extends BasicBoundPropertySet {

    private DataType dataType;
    private double euMin;
    private double euMax;
    private String engUnits;
    private String format;
    private String tooltip;
    private String documentation;
    private boolean historyEnabled;
    private String historyProvider;

    private SampleMode histSampleMode;
    private int histSampleRate;
    private DeadbandMode histDeadbandMode;
    private InterpolationMode histDeadbandStyle;
    private int histMaxAge;
    private TimeUnits histMaxAgeUnits;
    private TimeUnits histSampleRateUnits;
    private int histTimeDeadband;
    private TimeUnits histTimeDeadbandUnits;

    private TagConfig(){}

    public static TagConfig from(PublishTag tagConfig) {
        TagConfig config = new TagConfig();

        // tag configuration
        config.setDataType(tagConfig.dataType());
        config.setEuMin(tagConfig.euMin());
        config.setEuMax(tagConfig.euMax());
        config.setEngUnits(tagConfig.units());
        config.setFormat(tagConfig.format());
        config.setTooltip(tagConfig.tooltip());
        config.setDocumentation(tagConfig.documentation());

        // historical configuration
        config.setHistoryEnabled(tagConfig.historyEnabled());

        TagHistoryProviderRecord provider = null; // todo
        if(provider != null) config.setHistoryProvider(provider.getName());

        return config;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        set(WellKnownTagProps.DataType, dataType);
        this.dataType = dataType;
    }

    public double getEuMin() {
        return euMin;
    }

    public void setEuMin(double euMin) {
        set(WellKnownTagProps.EngLow, euMin);
        this.euMin = euMin;
    }

    public double getEuMax() {
        return euMax;
    }

    public void setEuMax(double euMax) {
        set(WellKnownTagProps.EngHigh, euMax);
        this.euMax = euMax;
    }

    public String getEngUnits() {
        return engUnits;
    }

    public void setEngUnits(String engUnits) {
        set(WellKnownTagProps.EngUnit, engUnits);
        this.engUnits = engUnits;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        set(WellKnownTagProps.FormatString, format);
        this.format = format;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        set(WellKnownTagProps.Tooltip, tooltip);
        this.tooltip = tooltip;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        set(WellKnownTagProps.Documentation, documentation);
        this.documentation = documentation;
    }

    public boolean isHistoryEnabled() {
        return historyEnabled;
    }

    public void setHistoryEnabled(boolean historyEnabled) {
        set(WellKnownTagProps.HistoryEnabled, historyEnabled);
        this.historyEnabled = historyEnabled;
    }

    public String getHistoryProvider() {
        return historyProvider;
    }

    public void setHistoryProvider(String historyProvider) {
        set(WellKnownTagProps.HistoryProvider, historyProvider);
        this.historyProvider = historyProvider;
    }

    public SampleMode getHistSampleMode() {
        return histSampleMode;
    }

    public void setHistSampleMode(SampleMode histSampleMode) {
        set(TagHistoryProps.HistorySampleMode, histSampleMode);
        this.histSampleMode = histSampleMode;
    }

    public int getHistSampleRate() {
        return histSampleRate;
    }

    public void setHistSampleRate(int histSampleRate) {
        set(TagHistoryProps.HistorySampleRate, histSampleRate);
        this.histSampleRate = histSampleRate;
    }

    public DeadbandMode getHistDeadbandMode() {
        return histDeadbandMode;
    }

    public void setHistDeadbandMode(DeadbandMode histDeadbandMode) {
        set(TagHistoryProps.HistoricalDeadbandMode, histDeadbandMode);
        this.histDeadbandMode = histDeadbandMode;
    }

    public InterpolationMode getHistDeadbandStyle() {
        return histDeadbandStyle;
    }

    public void setHistDeadbandStyle(InterpolationMode histDeadbandStyle) {
        set(TagHistoryProps.HistoricalDeadbandStyle, histDeadbandStyle);
        this.histDeadbandStyle = histDeadbandStyle;
    }

    public int getHistMaxAge() {
        return histMaxAge;
    }

    public void setHistMaxAge(int histMaxAge) {
        set(TagHistoryProps.HistoryMaxAge, histMaxAge);
        TagHistoryProps.HistoryMaxAgeUnits.getDefaultValue();
        this.histMaxAge = histMaxAge;
    }

    public TimeUnits getHistMaxAgeUnits() {
        return histMaxAgeUnits;
    }

    public void setHistMaxAgeUnits(TimeUnits histMaxAgeUnits) {
        set(TagHistoryProps.HistoryMaxAgeUnits, histMaxAgeUnits);
        this.histMaxAgeUnits = histMaxAgeUnits;
    }

    public TimeUnits getHistSampleRateUnits() {
        return histSampleRateUnits;
    }

    public void setHistSampleRateUnits(TimeUnits histSampleRateUnits) {
        this.histSampleRateUnits = histSampleRateUnits;
    }

    public int getHistTimeDeadband() {
        return histTimeDeadband;
    }

    public void setHistTimeDeadband(int histTimeDeadband) {
        this.histTimeDeadband = histTimeDeadband;
    }

    public TimeUnits getHistTimeDeadbandUnits() {
        return histTimeDeadbandUnits;
    }

    public void setHistTimeDeadbandUnits(TimeUnits histTimeDeadbandUnits) {
        this.histTimeDeadbandUnits = histTimeDeadbandUnits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TagConfig that = (TagConfig) o;

        if (Double.compare(that.euMin, euMin) != 0) return false;
        if (Double.compare(that.euMax, euMax) != 0) return false;
        if (historyEnabled != that.historyEnabled) return false;
        if (histSampleRate != that.histSampleRate) return false;
        if (histMaxAge != that.histMaxAge) return false;
        if (histTimeDeadband != that.histTimeDeadband) return false;
        if (dataType != that.dataType) return false;
        if (!Objects.equals(engUnits, that.engUnits)) return false;
        if (!Objects.equals(format, that.format)) return false;
        if (!Objects.equals(tooltip, that.tooltip)) return false;
        if (!Objects.equals(documentation, that.documentation)) return false;
        if (!Objects.equals(historyProvider, that.historyProvider)) return false;
        if (histSampleMode != that.histSampleMode) return false;
        if (histDeadbandMode != that.histDeadbandMode) return false;
        if (histDeadbandStyle != that.histDeadbandStyle) return false;
        if (histMaxAgeUnits != that.histMaxAgeUnits) return false;
        if (histSampleRateUnits != that.histSampleRateUnits) return false;
        return histTimeDeadbandUnits == that.histTimeDeadbandUnits;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = dataType != null ? dataType.hashCode() : 0;
        temp = Double.doubleToLongBits(euMin);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(euMax);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (engUnits != null ? engUnits.hashCode() : 0);
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (tooltip != null ? tooltip.hashCode() : 0);
        result = 31 * result + (documentation != null ? documentation.hashCode() : 0);
        result = 31 * result + (historyEnabled ? 1 : 0);
        result = 31 * result + (historyProvider != null ? historyProvider.hashCode() : 0);
        result = 31 * result + (histSampleMode != null ? histSampleMode.hashCode() : 0);
        result = 31 * result + histSampleRate;
        result = 31 * result + (histDeadbandMode != null ? histDeadbandMode.hashCode() : 0);
        result = 31 * result + (histDeadbandStyle != null ? histDeadbandStyle.hashCode() : 0);
        result = 31 * result + histMaxAge;
        result = 31 * result + (histMaxAgeUnits != null ? histMaxAgeUnits.hashCode() : 0);
        result = 31 * result + (histSampleRateUnits != null ? histSampleRateUnits.hashCode() : 0);
        result = 31 * result + histTimeDeadband;
        result = 31 * result + (histTimeDeadbandUnits != null ? histTimeDeadbandUnits.hashCode() : 0);
        return result;
    }
}
