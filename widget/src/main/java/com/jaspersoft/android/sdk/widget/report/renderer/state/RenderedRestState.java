package com.jaspersoft.android.sdk.widget.report.renderer.state;


import com.jaspersoft.android.sdk.network.entity.report.ReportParameter;
import com.jaspersoft.android.sdk.service.exception.StatusCodes;
import com.jaspersoft.android.sdk.service.report.ReportExecution;
import com.jaspersoft.android.sdk.widget.report.renderer.ChartType;
import com.jaspersoft.android.sdk.widget.report.renderer.Destination;
import com.jaspersoft.android.sdk.widget.report.renderer.Dispatcher;
import com.jaspersoft.android.sdk.widget.report.renderer.RenderState;
import com.jaspersoft.android.sdk.widget.report.renderer.ReportComponent;
import com.jaspersoft.android.sdk.widget.report.renderer.RunOptions;
import com.jaspersoft.android.sdk.widget.report.renderer.command.Command;
import com.jaspersoft.android.sdk.widget.report.renderer.command.CommandExecutor;
import com.jaspersoft.android.sdk.widget.report.renderer.command.CommandFactory;
import com.jaspersoft.android.sdk.widget.report.renderer.event.EventFactory;
import com.jaspersoft.android.sdk.widget.report.renderer.event.ExceptionEvent;
import com.jaspersoft.android.sdk.widget.report.renderer.event.ReportClearedEvent;
import com.jaspersoft.android.sdk.widget.report.renderer.event.ReportReadyEvent;
import com.jaspersoft.android.sdk.widget.report.renderer.event.ReportRenderedEvent;
import com.jaspersoft.android.sdk.widget.report.renderer.event.rest.PageExportedEvent;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * @author Andrew Tivodar
 * @since 2.6
 */
class RenderedRestState extends State {
    private final ReportExecution reportExecution;

    RenderedRestState(Dispatcher dispatcher, EventFactory eventFactory, CommandFactory commandFactory, CommandExecutor commandExecutor, ReportExecution reportExecution) {
        super(dispatcher, eventFactory, commandFactory, commandExecutor);
        this.reportExecution = reportExecution;
        waitForReportMetadata();
    }

    @Override
    protected void internalInit(double initialScale) {
        throw new IllegalStateException("Could not init. Already inited.");
    }

    @Override
    protected void internalRender(RunOptions runOptions) {
        throw new IllegalStateException("Could not render. Already rendered.");
    }

    @Override
    protected void internalApplyParams(List<ReportParameter> parameters) {
        setInProgress(true);
        Command applyParamsCommand = commandFactory.createApplyParamsCommand(parameters, reportExecution);
        commandExecutor.execute(applyParamsCommand);
    }

    @Override
    protected void internalNavigateTo(Destination destination) {
        setInProgress(true);
        Command navigateToCommand = commandFactory.createPageExportCommand(destination, reportExecution);
        commandExecutor.execute(navigateToCommand);
    }

    @Override
    protected void internalUpdateChartType(ReportComponent component, ChartType newChartType) {
        throw new IllegalStateException("Not available for REST Flow");
    }

    @Override
    protected void internalRefresh() {
        setInProgress(true);
        Command refreshCommand = commandFactory.createRefreshCommand(reportExecution);
        commandExecutor.execute(refreshCommand);
    }

    @Override
    protected void internalReset() {
        setInProgress(true);
        commandExecutor.cancelExecution();

        Command resetCommand = commandFactory.createResetCommand();
        commandExecutor.execute(resetCommand);
    }

    @Override
    public RenderState getName() {
        return RenderState.RENDERED;
    }

    private void waitForReportMetadata() {
        Command detectMultiPageCommand = commandFactory.createDetectMultiPageCommand(reportExecution);
        commandExecutor.execute(detectMultiPageCommand);

        Command waitForReportMetadataCommand = commandFactory.createWaitForReportMetadataCommand(reportExecution);
        commandExecutor.execute(waitForReportMetadataCommand);
    }

    @Subscribe
    public void onReportReady(ReportReadyEvent reportReadyEvent) {
        Command pageExportCommand = commandFactory.createPageExportCommand(new Destination(1), reportExecution);
        commandExecutor.execute(pageExportCommand);
        waitForReportMetadata();
    }

    @Subscribe
    public void onPageExported(PageExportedEvent pageExportedEvent) {
        Command showPageCommand = commandFactory.createShowPageCommand(pageExportedEvent.getReportPage(), pageExportedEvent.getPageNumber(), reportExecution.getExecutionId());
        commandExecutor.execute(showPageCommand);
    }

    @Subscribe
    public void onReportRendered(ReportRenderedEvent reportRenderedEvent) {
        setInProgress(false);
    }

    @Subscribe
    public void onReportCleared(ReportClearedEvent reportClearedEvent) {
        setInProgress(false);
        dispatcher.dispatch(eventFactory.createSwapStateEvent(RenderState.INITED));
    }

    @Subscribe
    public void onError(ExceptionEvent exceptionEvent) {
        setInProgress(false);
        if (exceptionEvent.getException().code() == StatusCodes.AUTHORIZATION_ERROR) {
            dispatcher.dispatch(eventFactory.createSwapStateEvent(RenderState.INITED));
        }
    }
}
