package com.fieldservicemanagement.keystone.scheduler;

import com.fieldservicemanagement.keystone.service.SlaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SlaBreachCheckerTest {

    @Mock
    private SlaService slaService;

    @InjectMocks
    private SlaBreachChecker slaBreachChecker;

    @Test
    void checkSlaBreaches_shouldInvokeScanForBreaches() {
        slaBreachChecker.checkForBreaches();

        verify(slaService).scanForBreaches();
    }

    @Test
    void checkSlaBreaches_shouldHandleExceptionGracefully() {
        doThrow(new RuntimeException("Database unavailable")).when(slaService).scanForBreaches();

        assertThatCode(() -> slaBreachChecker.checkForBreaches())
                .doesNotThrowAnyException();

        verify(slaService).scanForBreaches();
    }

    @Test
    void checkSlaBreaches_scheduledAnnotation_shouldBePresent() throws NoSuchMethodException {
        Method method = SlaBreachChecker.class.getDeclaredMethod("checkSlaBreaches");
        Scheduled scheduled = method.getAnnotation(Scheduled.class);

        assertThat(scheduled).isNotNull();
        assertThat(scheduled.cron()).isEqualTo("0 0 * * * *");
    }
}
