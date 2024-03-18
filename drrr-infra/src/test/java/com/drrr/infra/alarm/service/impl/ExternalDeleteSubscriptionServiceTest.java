package com.drrr.infra.alarm.service.impl;
import static org.assertj.core.api.Assertions.assertThat;
import com.drrr.alarm.service.impl.ExternalDeleteSubscriptionService;
import com.drrr.domain.fixture.member.MemberFixture;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.infra.fixture.SubscriptionFixture;
import com.drrr.infra.push.entity.Subscription;
import com.drrr.infra.push.repository.SubscriptionRepository;
import com.drrr.infra.push.service.SubscriptionService;
import com.drrr.infra.util.ServiceInfraIntegrationTest;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;

@ComponentScan(basePackages = "com.drrr.infra", includeFilters = @ComponentScan.Filter(Service.class))
@SpringBootTest(classes = {MemberRepository.class, ExternalDeleteSubscriptionService.class})
class ExternalDeleteSubscriptionServiceTest extends ServiceInfraIntegrationTest  {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private ExternalDeleteSubscriptionService externalDeleteSubscriptionService;

    @Autowired
    private EntityManager em;
    @Test
    void 구독정보가_정상적으로_삭제됩니다() {
        //given
        Member member = MemberFixture.createMember();
        memberRepository.save(member);

        subscriptionRepository.save(SubscriptionFixture.createSubscription(member));

        em.clear();
        em.flush();

        // When
        externalDeleteSubscriptionService.execute(member.getId());

        // Then
        Optional<Subscription> optionalMemberId = subscriptionRepository.findByMemberId(member.getId());
        assertThat(optionalMemberId).isEmpty();
    }

}