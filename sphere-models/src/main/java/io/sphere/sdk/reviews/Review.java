package io.sphere.sdk.reviews;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.models.Reference;
import io.sphere.sdk.models.Resource;
import io.sphere.sdk.states.State;
import io.sphere.sdk.types.Custom;
import io.sphere.sdk.types.CustomFields;

import javax.annotation.Nullable;
import java.util.Locale;

/**
  Reviews are used to evaluate products and channels.

  <h3 id=review-approval-process>Review Approval Process</h3>

  <p>If you do not need any approval process, skip this part.</p>

 <p>If we have an approval process for a review to be used for a product or a channel, we model the approval process with a state machine.</p>

 <p>First of all, we create the approved state. Then we create the initial to-approve state, which has a possible transition to the approved state:</p>

 {@include.example io.sphere.sdk.reviews.approvaldemo.CreateReviewStates}

 Only states with {@link io.sphere.sdk.states.StateRole#REVIEW_INCLUDED_IN_STATISTICS} makes review ratings count in statistics.

 <h4 id=creating-reviews>Creating reviews</h4>
 <p>Now we can create a review in the initial state to-approve:</p>

 {@include.example io.sphere.sdk.reviews.approvaldemo.CreateReviewToApprove}

 <h4 id=query-to-approve-reviews>Query which reviews should be approved</h4>

 {@include.example io.sphere.sdk.reviews.approvaldemo.QueryReviewsToApprove}

 <h4 id=approve-a-review>Approving a review</h4>


 {@include.example io.sphere.sdk.reviews.approvaldemo.ApprovingAReview}

<h3 id=displaying-products>Displaying Products</h3>
<h4 id=search-products-for-minimal-rating>Searching for Products with a Minimal Rating</h4>
 <p>We can display all products that:</p>
<ul>
 <li>have at least 2 stars (average rating superior to 2)</li>
 <li>with facets about the number of products rated with an average in the different ranges 0 to 1 star, 1 to 2 stars, 2 to 3 stars, 3 to 4 stars and 4 to 5 stars.</li>
 <li>sorted by average ratings</li>
 </ul>

 {@include.example io.sphere.sdk.reviews.ReviewProductProjectionSearchTest#searchForReviewsWithAverageRatingGreaterThan2()}

 <h4 id=query-approved-reviews-for-one-product>Getting reviews for one product (only approved)</h4>
 {@include.example io.sphere.sdk.reviews.ReviewProductProjectionSearchTest#getApprovedReviewsForOneProduct()}

 <h4 id=query-reviews-for-one-product>Getting reviews for one product</h4>
 {@include.example io.sphere.sdk.reviews.ReviewProductProjectionSearchTest#getReviewsForOneProduct()}

 */
@JsonDeserialize(as = ReviewImpl.class)
public interface Review extends Resource<Review>, Custom {
    @Nullable
    String getAuthorName();

    @Nullable
    CustomFields getCustom();

    @Nullable
    Reference<Customer> getCustomer();

    @Nullable
    String getKey();

    @Nullable
    Locale getLocale();

    @Nullable
    Integer getRating();

    @Nullable
    Reference<State> getState();

    /**
     * Identifies the target of the review. Can be a Product or a Channel or nothing.
     * @return the target of this review or null
     */
    @Nullable
    Reference<?> getTarget();

    @Nullable
    String getText();

    @Nullable
    String getTitle();

    @Nullable
    String getUniquenessValue();

    /**
     * Indicates if this review is taken into account in the ratings statistics of the target.
     A review is per default used in the statistics, unless the review is in a state that does not have the role ReviewIncludedInStatistics.
     If the role of a State is modified after the calculation of this field, the calculation is not updated.
     * @return is included in review rating of target
     */
    Boolean isIncludedInStatistics();

    static String referenceTypeId() {
        return "review";
    }

    static String resourceTypeId() {
        return "review";
    }

    static TypeReference<Review> typeReference(){
        return new TypeReference<Review>() {
            @Override
            public String toString() {
                return "TypeReference<Review>";
            }
        };
    }

    static Reference<Review> referenceOfId(final String id) {
        return Reference.of(referenceTypeId(), id);
    }

    default Reference<Review> toReference() {
        return Reference.of(referenceTypeId(), getId(), this);
    }
}
