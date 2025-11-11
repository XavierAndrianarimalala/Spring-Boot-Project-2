package com.finance.service;

import com.finance.dto.goal.GoalRequest;
import com.finance.dto.goal.GoalResponse;
import com.finance.entity.Account;
import com.finance.entity.Goal;
import com.finance.entity.Goal.GoalPriority;
import com.finance.entity.Goal.GoalStatus;
import com.finance.entity.User;
import com.finance.exception.ResourceNotFoundException;
import com.finance.exception.UnauthorizedException;
import com.finance.mapper.GoalMapper;
import com.finance.repository.AccountRepository;
import com.finance.repository.GoalRepository;
import com.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final GoalMapper goalMapper;

    @Transactional
    public GoalResponse createGoal(GoalRequest request, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Goal goal = Goal.builder()
            .name(request.name())
            .description(request.description())
            .targetAmount(request.targetAmount())
            .currentAmount(request.currentAmount() != null ? request.currentAmount() : BigDecimal.ZERO)
            .targetDate(request.targetDate())
            .status(GoalStatus.IN_PROGRESS)
            .priority(request.priority() != null ? request.priority() : GoalPriority.MEDIUM)
            .icon(request.icon())
            .color(request.color())
            .user(user)
            .build();

        // Link to account if specified
        if (request.accountId() != null) {
            Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.accountId()));
            validateAccountOwnership(account, username);
            goal.setAccount(account);
        }

        Goal savedGoal = goalRepository.save(goal);
        return goalMapper.toResponse(savedGoal);
    }

    public GoalResponse getGoalById(Long id, String username) {
        Goal goal = goalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        validateGoalOwnership(goal, username);
        return goalMapper.toResponse(goal);
    }

    public List<GoalResponse> getAllGoals(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return goalRepository.findByUserIdOrderByTargetDateAsc(user.getId())
            .stream()
            .map(goalMapper::toResponse)
            .collect(Collectors.toList());
    }

    public List<GoalResponse> getGoalsByStatus(String username, GoalStatus status) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return goalRepository.findByUserIdAndStatus(user.getId(), status)
            .stream()
            .map(goalMapper::toResponse)
            .collect(Collectors.toList());
    }

    public List<GoalResponse> getActiveGoals(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return goalRepository.findActiveGoalsByUserIdOrderedByPriority(user.getId())
            .stream()
            .map(goalMapper::toResponse)
            .collect(Collectors.toList());
    }

    public List<GoalResponse> getOverdueGoals(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return goalRepository.findOverdueGoalsByUserId(user.getId(), LocalDate.now())
            .stream()
            .map(goalMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public GoalResponse updateGoal(Long id, GoalRequest request, String username) {
        Goal goal = goalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        validateGoalOwnership(goal, username);

        goal.setName(request.name());
        goal.setDescription(request.description());
        goal.setTargetAmount(request.targetAmount());
        goal.setTargetDate(request.targetDate());

        if (request.priority() != null) {
            goal.setPriority(request.priority());
        }

        goal.setIcon(request.icon());
        goal.setColor(request.color());

        // Update account if specified
        if (request.accountId() != null) {
            Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.accountId()));
            validateAccountOwnership(account, username);
            goal.setAccount(account);
        } else {
            goal.setAccount(null);
        }

        // Auto-update status if target reached
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        }

        Goal updatedGoal = goalRepository.save(goal);
        return goalMapper.toResponse(updatedGoal);
    }

    @Transactional
    public GoalResponse updateProgress(Long id, BigDecimal amount, String username) {
        Goal goal = goalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        validateGoalOwnership(goal, username);

        goal.setCurrentAmount(amount);

        // Auto-update status if target reached
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0
            && goal.getStatus() == GoalStatus.IN_PROGRESS) {
            goal.setStatus(GoalStatus.COMPLETED);
        }

        Goal updatedGoal = goalRepository.save(goal);
        return goalMapper.toResponse(updatedGoal);
    }

    @Transactional
    public GoalResponse addProgress(Long id, BigDecimal amount, String username) {
        Goal goal = goalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        validateGoalOwnership(goal, username);

        BigDecimal newAmount = goal.getCurrentAmount().add(amount);
        goal.setCurrentAmount(newAmount);

        // Auto-update status if target reached
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0
            && goal.getStatus() == GoalStatus.IN_PROGRESS) {
            goal.setStatus(GoalStatus.COMPLETED);
        }

        Goal updatedGoal = goalRepository.save(goal);
        return goalMapper.toResponse(updatedGoal);
    }

    @Transactional
    public GoalResponse updateStatus(Long id, GoalStatus status, String username) {
        Goal goal = goalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        validateGoalOwnership(goal, username);

        goal.setStatus(status);
        Goal updatedGoal = goalRepository.save(goal);
        return goalMapper.toResponse(updatedGoal);
    }

    @Transactional
    public void deleteGoal(Long id, String username) {
        Goal goal = goalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));
        validateGoalOwnership(goal, username);
        goalRepository.delete(goal);
    }

    private void validateGoalOwnership(Goal goal, String username) {
        if (!goal.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You don't have permission to access this goal");
        }
    }

    private void validateAccountOwnership(Account account, String username) {
        if (!account.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You don't have permission to access this account");
        }
    }
}
