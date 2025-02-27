package shop.mtcoding.metamall.model.ordersheet;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.mtcoding.metamall.model.orderproduct.OrderProduct;
import shop.mtcoding.metamall.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Table(name = "order_sheet_tb")
@Entity
@NoArgsConstructor
public class OrderSheet { // 주문서
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // 주문자
    @OneToMany(mappedBy = "orderSheet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProductList = new ArrayList<>(); // 총 주문 상품 리스트
    private Integer totalPrice; // 총 주문 금액 (총 주문 상품 리스트의 orderPrice 합)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // 연관관계 메서드 구현 필요

    @Builder
    public OrderSheet(Long id, User user, Integer totalPrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
